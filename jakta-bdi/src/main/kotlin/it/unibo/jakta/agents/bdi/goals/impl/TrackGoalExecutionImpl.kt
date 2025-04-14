package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

internal class TrackGoalExecutionImpl(
    override val planID: PlanID,
    override val goal: Goal,
) : TrackGoalExecution {
    override val value: Struct
        get() = goal.value

    override fun applySubstitution(substitution: Substitution) =
        TrackGoalExecutionImpl(planID, goal.applySubstitution(substitution))

    override fun toString(): String = "TrackGoalExecution($goal)"

    override fun copy(value: Struct): Goal = TrackGoalExecutionImpl(planID, goal.copy(value = value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackGoalExecutionImpl

        if (planID != other.planID) return false
        if (goal != other.goal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = planID.hashCode()
        result = 31 * result + goal.hashCode()
        return result
    }
}
