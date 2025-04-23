package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.goals.Goal

sealed interface GoalSuccess : PositiveFeedback {
    data class GoalExecutionSuccess(
        val goalExecuted: Goal,
    ) : GoalSuccess {
        override val description =
            "Goal $goalExecuted executed successfully"
    }
}
