package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
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
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultBuilder
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GoalGenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GoalGenerationStrategy.Companion.MAX_GENERATION_ATTEMPTS
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID

class GoalGenerationStrategyImpl(
    genResProcessor: GenerationResultBuilder,
    override val loggingConfig: LoggingConfig?,
    override val logger: KLogger?,
) : GoalGenerationStrategy {

    private val generationProcessPlanBuilder = GenerationProcessPlanBuilder(logger)
    private val stateManager = GenerationStateManager(loggingConfig, logger)
    private val caseHandler = GenerationCaseHandler(genResProcessor)
    private val errorHandler = GenerationErrorHandler()

    override fun generateGoal(
        genGoal: Generate,
        intention: Intention,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult {
        logger?.info { "Handling $genGoal" }

        if (context.generationProcesses.size > MAX_CONCURRENT_GENERATION_REQUESTS) {
            return errorHandler.handleMaxConcurrentGenerationProcessesExceeded(intention, context)
        }

        val plan = generationProcessPlanBuilder.findOrCreatePlan(genGoal, intention, context)

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
            is FailureResult -> errorHandler.handleFailure(intention, context, planGenResult)

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
        if (planGenResult.generationState.failedGenerationProcess >= MAX_GENERATION_ATTEMPTS) {
            return errorHandler.handleMaxAttemptsExceeded(intention, context, MAX_GENERATION_ATTEMPTS)
        }

        val genPlanID = context.generationProcesses.keys.firstOrNull {
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
                    genGoal,
                    intention,
                    context,
                    plan,
                    updatedPlanGenResult,
                )

            shouldStartNewGenerationAsChild(intention, genPlanID) ->
                caseHandler.handleNewChildGeneration(
                    genGoal,
                    intention as DeclarativeIntention,
                    context,
                    plan,
                    updatedPlanGenResult,
                )

            shouldContinueExistingGeneration(intention, genPlanID) ->
                caseHandler.handleExistingGeneration(
                    genGoal,
                    intention as DeclarativeIntention,
                    context,
                    plan,
                    updatedPlanGenResult,
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
