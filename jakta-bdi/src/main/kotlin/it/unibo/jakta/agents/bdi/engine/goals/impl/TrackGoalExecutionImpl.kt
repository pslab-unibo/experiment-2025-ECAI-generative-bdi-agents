package it.unibo.jakta.agents.bdi.engine.goals.impl

import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("TrackGoalExecution")
internal class TrackGoalExecutionImpl(
    override val goal: Goal,
) : TrackGoalExecution {
    override val value: SerializableStruct get() = goal.value

    override fun applySubstitution(substitution: Substitution) =
        TrackGoalExecutionImpl(goal.applySubstitution(substitution))

    override fun toString(): String = "TrackGoalExecution(${goalFormatter.format(goal)})"

    override fun copy(value: Struct): Goal = TrackGoalExecutionImpl(goal.copy(value = value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackGoalExecutionImpl

        return goal == other.goal
    }

    override fun hashCode(): Int = goal.hashCode()
}
