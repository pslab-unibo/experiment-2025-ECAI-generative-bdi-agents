package it.unibo.jakta.agents.bdi.plangeneration.feedback

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.plangeneration.Common.getStrategyFromID
import it.unibo.jakta.agents.bdi.plangeneration.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.plans.PlanID

object FeedbackUpdater {
    fun updateGenerationStateWithFeedback(
        generatingPlanID: PlanID?,
        executionResult: ExecutionResult,
        logger: KLogger?,
    ): ExecutionResult =
        if (executionResult.feedback != null) {
            executionResult.copy(
                executionResult.newAgentContext.copy(
                    generationProcess = provideFeedback(
                        generatingPlanID,
                        executionResult.feedback,
                        executionResult.newAgentContext,
                        logger,
                    ),
                ),
            )
        } else {
            executionResult
        }

    private fun provideFeedback(
        generatedPlanID: PlanID?,
        feedback: ExecutionFeedback,
        context: AgentContext,
        logger: KLogger?,
    ): GenerationProcessRegistry {
        return if (generatedPlanID != null) {
            val strategy = getStrategyFromID(generatedPlanID, context.planLibrary)
            if (strategy != null) {
                val state = context.generationProcesses[generatedPlanID]
                if (state != null) {
                    val updatedState = strategy.provideGenerationFeedback(state, feedback)
                    context.generationProcesses.updateGenerationProcess(generatedPlanID, updatedState)
                } else {
                    logger?.error { "No generation state found for planID=$generatedPlanID" }
                    context.generationProcesses
                }
            } else {
                logger?.error { "No generation strategy found for planID=$generatedPlanID" }
                context.generationProcesses
            }
        } else {
            logger?.error { "No generating plan with id=$generatedPlanID found in the current intention" }
            context.generationProcesses
        }
    }
}
