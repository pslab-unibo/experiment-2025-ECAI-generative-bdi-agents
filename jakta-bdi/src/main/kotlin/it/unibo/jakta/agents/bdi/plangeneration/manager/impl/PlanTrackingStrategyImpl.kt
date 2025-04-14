package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.TrackPlanExecution
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GoalExecutionSuccess
import it.unibo.jakta.agents.bdi.plangeneration.feedback.PlanExecutionCompleted
import it.unibo.jakta.agents.bdi.plangeneration.manager.GenerationManager.Companion.getGoalsAchieved
import it.unibo.jakta.agents.bdi.plangeneration.manager.PlanTrackingStrategy

class PlanTrackingStrategyImpl(
    override val logger: KLogger?,
) : PlanTrackingStrategy {

    override fun trackPlanExecution(
        goal: TrackPlanExecution,
        intention: Intention,
        context: AgentContext,
    ): ExecutionResult {
        /**
         * Only provide feedback if a generating plan completes.
         */
        return if (intention is DeclarativeIntention && intention.generatingPlans.contains(goal.planID)) {
            provideFeedbackForCompletedPlan(goal, intention, context)
        } else {
            ExecutionResult(
                context.copy(intentions = context.intentions.updateIntention(intention.pop())),
                GoalExecutionSuccess(goal),
            )
        }
    }

    private fun provideFeedbackForCompletedPlan(
        goal: TrackPlanExecution,
        intention: DeclarativeIntention,
        context: AgentContext,
    ): ExecutionResult {
        // Report the goals achieved so far.
        val goalsAchieved = getGoalsAchieved(intention, context)
        val feedback = PlanExecutionCompleted(goal.planID, goalsAchieved)

        // Clear the goals achieved so far.
        val updatedState = context.generationRequests[intention.currentGeneratingPlan()]
        val updatedGenRequests = if (updatedState != null) {
            context.generationRequests.updateRequest(
                intention.currentPlan(),
                updatedState.copy(
                    achievedGoalsHistory = emptyList(),
                ),
            )
        } else {
            context.generationRequests
        }

        return ExecutionResult(
            context.copy(
                generationRequests = updatedGenRequests,
                intentions = context.intentions.updateIntention(intention.pop()),
            ),
            feedback = feedback,
        )
    }
}
