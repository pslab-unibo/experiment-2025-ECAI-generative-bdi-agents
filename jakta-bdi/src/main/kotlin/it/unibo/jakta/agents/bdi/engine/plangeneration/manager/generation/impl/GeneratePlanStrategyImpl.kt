package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.PGPSuccess
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationFailureResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.GeneratePlanStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.GenerationResultBuilder

internal class GeneratePlanStrategyImpl(
    private val genResProcessor: GenerationResultBuilder,
    override val loggingConfig: LoggingConfig?,
    override val logger: AgentLogger?,
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

        val updatedGenerationStrategy =
            genGoal.generationConfig
                ?.let { generationStrategy.updateGenerationConfig(it) }
                ?: generationStrategy

        val generationState =
            stateManager.setupGenerationStart(
                context,
                updatedGenerationStrategy,
                genGoal,
                environment,
            )

        /*
         * Request the plan generation.
         */
        val planGenResult = updatedGenerationStrategy.requestBlockingGeneration(generationState)

        return when (planGenResult) {
            is GenerationFailureResult -> errorHandler.handleFailure(intention, context, planGenResult)
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
        genResProcessor
            .buildResult(
                genGoal,
                context,
                intention,
                planGenResult,
            ).copy(
                feedback =
                    PGPSuccess.GenerationCompleted(
                        genGoal,
                        planGenResult.generatedPlanLibrary,
                        planGenResult.generatedAdmissibleGoals,
                        planGenResult.generatedAdmissibleBeliefs,
                    ),
            )
}
