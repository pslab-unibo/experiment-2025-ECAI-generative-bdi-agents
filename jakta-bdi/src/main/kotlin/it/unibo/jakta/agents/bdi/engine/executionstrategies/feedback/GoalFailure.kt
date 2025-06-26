package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import it.unibo.tuprolog.core.Struct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("GoalFailure")
sealed interface GoalFailure : NegativeFeedback {
    @Serializable
    @SerialName("TestGoalFailureFeedback")
    data class TestGoalFailureFeedback(
        val goalTested: Struct,
        override val description: String?,
    ) : GoalFailure {
        constructor(goalTested: Struct) : this(goalTested, "The goal $goalTested could not be tested")
    }
}
