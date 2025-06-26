package it.unibo.jakta.agents.bdi.engine.generation.manager.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.GoalSuccess
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.NegativeFeedback
import it.unibo.jakta.agents.bdi.engine.generation.manager.GoalTrackingStrategy
import it.unibo.jakta.agents.bdi.engine.generation.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.engine.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.intentions.Intention.Companion.replace
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger

internal class GoalTrackingStrategyImpl(
    override val invalidationStrategy: InvalidationStrategy,
    override val logger: AgentLogger?,
) : GoalTrackingStrategy {
    /**
     * Replace the previous intention's [TrackGoalExecution] with the goal being tracked.
     * If the execution is successful, also update the plan library by un-tracking the goal.
     * Otherwise, invalidate the plan library, the intention and any event and report the failure.
     */
    override fun trackGoalExecution(
        genGoal: TrackGoalExecution,
        intention: Intention,
        context: AgentContext,
        environment: Environment,
        runIntention: (Intention, AgentContext, Environment) -> ExecutionResult,
    ): ExecutionResult {
        val goalToExecute = genGoal.goal
        val newIntention = intention.replace(genGoal, goalToExecute)

        val result = runIntention(newIntention, context, environment)

        val updatedRes =
            if (result.feedback is NegativeFeedback) {
                result
            } else {
                logger?.log { GoalSuccess.GoalExecutionSuccess(genGoal) }
                val updatedPlanLibrary = genGoal.untrack(intention.currentPlan(), result.newAgentContext.planLibrary)
                result.copy(
                    newAgentContext =
                        result.newAgentContext.copy(
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
