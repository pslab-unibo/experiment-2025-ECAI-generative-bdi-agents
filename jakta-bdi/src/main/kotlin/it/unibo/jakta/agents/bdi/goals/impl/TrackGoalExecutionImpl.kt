package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

internal class TrackGoalExecutionImpl(
    override val goal: Goal,
) : TrackGoalExecution {
    override val value: Struct
        get() = goal.value

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

    override fun hashCode(): Int {
        return goal.hashCode()
    }
}
