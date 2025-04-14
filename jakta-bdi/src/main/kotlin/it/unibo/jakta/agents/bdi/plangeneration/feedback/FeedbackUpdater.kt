package it.unibo.jakta.agents.bdi.plangeneration.feedback

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.plangeneration.Common.getStrategyFromID
import it.unibo.jakta.agents.bdi.plangeneration.pool.GenerationRequestPool
import it.unibo.jakta.agents.bdi.plans.PlanID

object FeedbackUpdater {
    fun updateGenerationStateWithFeedback(
        generatingPlanID: PlanID?,
        executionResult: ExecutionResult,
        logger: KLogger?,
        predicate: (ExecutionFeedback) -> Boolean = { true },
    ): ExecutionResult =
        if (executionResult.feedback != null && predicate(executionResult.feedback)) {
            executionResult.copy(
                executionResult.newAgentContext.copy(
                    generationRequests = provideFeedback(
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
    ): GenerationRequestPool {
        return if (generatedPlanID != null) {
            val strategy = getStrategyFromID(generatedPlanID, context.planLibrary)
            if (strategy != null) {
                val state = context.generationRequests[generatedPlanID]
                if (state != null) {
                    val updatedState = strategy.provideGenerationFeedback(state, feedback)
                    context.generationRequests.updateRequest(generatedPlanID, updatedState)
                } else {
                    logger?.error { "No generation state found for planID=$generatedPlanID" }
                    context.generationRequests
                }
            } else {
                logger?.error { "No generation strategy found for planID=$generatedPlanID" }
                context.generationRequests
            }
        } else {
            logger?.error { "No generating plan with id=$generatedPlanID found in the current intention" }
            context.generationRequests
        }
    }
}
