package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.NegativeFeedback
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.intentions.Intention.Companion.replace
import it.unibo.jakta.agents.bdi.plangeneration.manager.GoalTrackingStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.InvalidationStrategy

class GoalTrackingStrategyImpl(
    override val invalidationStrategy: InvalidationStrategy,
    override val logger: KLogger?,
) : GoalTrackingStrategy {

    /**
     * Replace the previous intention's [TrackGoalExecution] with the goal being tracked.
     * If the execution is successful, also update the plan library by un-tracking the goal.
     * Otherwise, invalidate the plan library, intention and any event and return the failure feedback.
     */
    override fun trackGoalExecution(
        goal: TrackGoalExecution,
        intention: Intention,
        context: AgentContext,
        environment: Environment,
        runIntention: (Intention, AgentContext, Environment) -> ExecutionResult,
    ): ExecutionResult {
        logger?.info { "Tracking execution of goal ${goal.goal}" }

        val goalToExecute = goal.goal
        val newIntention = intention.replace(goal, goalToExecute)

        val result = runIntention(newIntention, context, environment)

        val updatedRes = if (result.feedback is NegativeFeedback) {
            result
        } else {
            val updatedPlanLibrary = goal.untrack(intention.currentPlan(), result.newAgentContext.planLibrary)
            result.copy(
                newAgentContext = result.newAgentContext.copy(
                    planLibrary = updatedPlanLibrary,
                ),
            )
        }

        return if (updatedRes.feedback is NegativeFeedback) {
            invalidationStrategy.invalidate(intention.currentPlan(), updatedRes.newAgentContext).copy(
                feedback = updatedRes.feedback,
            )
        } else {
            updatedRes
        }
    }
}
