package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.FailureResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.InfiniteRecursionGuardConfig.MAX_CONCURRENT_GENERATION_REQUESTS
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenerationExecuted
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultProcessor
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GoalGenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GoalGenerationStrategy.Companion.MAX_GENERATION_ATTEMPTS
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID

class GoalGenerationStrategyImpl(
    genResProcessor: GenerationResultProcessor,
    override val loggingConfig: LoggingConfig?,
    override val logger: KLogger?,
) : GoalGenerationStrategy {

    private val planLocator = PlanLocator(logger)
    private val stateManager = GenerationStateManager(loggingConfig, logger)
    private val caseHandler = GenerationCaseHandler(genResProcessor)
    private val errorHandler = GenerationErrorHandler()

    override fun generateGoal(
        genGoal: Generate,
        intention: Intention,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult {
        if (context.generationRequests.size > MAX_CONCURRENT_GENERATION_REQUESTS) {
            return errorHandler.handleMaxConcurrentGenerationRequestExceeded(intention, context)
        }

        val plan = planLocator.findOrCreatePlan(genGoal, intention, context)

        val generationStrategy = plan.generationStrategy
        if (generationStrategy == null) {
            return errorHandler.handleMissingGenerationStrategy(intention, context)
        }

        val generationState = stateManager.initializeOrReuseGenerationState(
            context,
            intention,
            plan,
            generationStrategy,
            genGoal,
            environment,
        )

        /*
         * Request the plan generation.
         */
        val planGenResult = generationStrategy.requestBlockingGeneration(plan, generationState)

        return when (planGenResult) {
            is FailureResult ->
                errorHandler.handleFailure(intention, context, planGenResult)

            is PlanGenerationResult ->
                processPlanGenerationResult(
                    context,
                    intention,
                    genGoal,
                    plan,
                    generationStrategy,
                    planGenResult,
                )

            else -> errorHandler.handleUnknownResult(intention, context)
        }
    }

    private fun processPlanGenerationResult(
        context: AgentContext,
        intention: Intention,
        genGoal: Generate,
        plan: PartialPlan,
        generationStrategy: GenerationStrategy,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        if (planGenResult.generationState.failedGenerationRequests >= MAX_GENERATION_ATTEMPTS) {
            return errorHandler.handleMaxAttemptsExceeded(intention, context, MAX_GENERATION_ATTEMPTS)
        }

        val genPlanID = context.generationRequests.keys.firstOrNull {
            it.trigger.value == genGoal.value
        }

        val updatedPlanGenResult = stateManager.checkIfGenerationFinished(
            planGenResult,
            plan,
            generationStrategy,
            context,
        )

        return when {
            shouldStartNewGenerationAsRoot(intention, genPlanID) ->
                caseHandler.handleNewRootGeneration(
                    intention,
                    context,
                    plan,
                    updatedPlanGenResult,
                ).copy(
                    feedback = if (updatedPlanGenResult.generationState.isGenerationEndConfirmed) {
                        GenerationExecuted("Terminated the generation process")
                    } else {
                        GenerationExecuted("Started a new generation process as the root.")
                    },
                )

            shouldStartNewGenerationAsChild(intention, genPlanID) ->
                caseHandler.handleNewChildGeneration(
                    intention as DeclarativeIntention,
                    context,
                    plan,
                    updatedPlanGenResult,
                ).copy(
                    feedback = if (updatedPlanGenResult.generationState.isGenerationEndConfirmed) {
                        GenerationExecuted("Terminated the generation process")
                    } else {
                        GenerationExecuted("Started a new generation process as a child.")
                    },
                )

            shouldContinueExistingGeneration(intention, genPlanID) ->
                caseHandler.handleExistingGeneration(
                    intention as DeclarativeIntention,
                    context,
                    plan,
                    updatedPlanGenResult,
                ).copy(
                    feedback = if (updatedPlanGenResult.generationState.isGenerationEndConfirmed) {
                        GenerationExecuted("Terminated the generation process")
                    } else {
                        GenerationExecuted(
                            "Continued the existing generation process for ${formatter.format(plan.id.trigger.value)}",
                        )
                    },
                )

            else -> errorHandler.handleMissingDeclarativeIntention(intention, context)
        }
    }

    companion object {
        fun shouldStartNewGenerationAsRoot(intention: Intention, genPlanID: PlanID?): Boolean =
            intention !is DeclarativeIntention && genPlanID == null

        fun shouldStartNewGenerationAsChild(intention: Intention, genPlanID: PlanID?): Boolean =
            intention is DeclarativeIntention && genPlanID == null

        fun shouldContinueExistingGeneration(intention: Intention, genPlanID: PlanID?): Boolean =
            intention is DeclarativeIntention && genPlanID != null
    }
}
