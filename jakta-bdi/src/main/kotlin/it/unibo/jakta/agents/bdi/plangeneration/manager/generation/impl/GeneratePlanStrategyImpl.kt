package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.PositiveFeedback.GenerationCompleted
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationFailureResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GeneratePlanStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultBuilder

class GeneratePlanStrategyImpl(
    private val genResProcessor: GenerationResultBuilder,
    override val loggingConfig: LoggingConfig?,
    override val logger: KLogger?,
) : GeneratePlanStrategy {

    private val stateManager = GenerationStateInitializer(loggingConfig, logger)
    private val errorHandler = GenerationErrorHandler(logger)

    override fun generatePlan(
        genGoal: GeneratePlan,
        intention: Intention,
        generationStrategy: GenerationStrategy,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult {
        logger?.info { "Started the plan generation procedure for goal $genGoal" }

        val generationState = stateManager.setupGenerationStart(
            context,
            generationStrategy,
            genGoal,
            environment,
        )

        /*
         * Request the plan generation.
         */
        val planGenResult = generationStrategy.requestBlockingGeneration(generationStrategy, generationState)

        return when (planGenResult) {
            is GenerationFailureResult -> errorHandler.handleFailure(genGoal, intention, context, planGenResult)
            is PlanGenerationResult -> processPlanGenerationResult(genGoal, intention, context, planGenResult)
            else -> errorHandler.handleUnknownResult(intention, context)
        }
    }

    private fun processPlanGenerationResult(
        genGoal: GeneratePlan,
        intention: Intention,
        context: AgentContext,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult =
        genResProcessor.buildResult(
            genGoal,
            context,
            intention,
            planGenResult,
        ).copy(
            feedback = GenerationCompleted(
                genGoal,
                planGenResult.generatedPlanLibrary,
                planGenResult.generatedAdmissibleGoals,
                planGenResult.generatedAdmissibleBeliefs,
            ),
        )
}
