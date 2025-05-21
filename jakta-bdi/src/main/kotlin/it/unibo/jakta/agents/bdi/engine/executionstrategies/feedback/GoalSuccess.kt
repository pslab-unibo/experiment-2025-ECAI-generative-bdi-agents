package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.engine.goals.Goal
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("GoalSuccess")
sealed interface GoalSuccess : PositiveFeedback {
    @Serializable
    @SerialName("GoalExecutionSuccess")
    data class GoalExecutionSuccess(
        val goalExecuted: Goal,
        override val description: String,
    ) : GoalSuccess {
        constructor(goalExecuted: Goal) : this(
            goalExecuted,
            "The goal $goalExecuted was successfully executed",
        )
    }
}
