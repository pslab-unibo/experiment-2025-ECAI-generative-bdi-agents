package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.TrackPlanExecution
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

class TrackPlanExecutionImpl(override val planID: PlanID) : TrackPlanExecution {
    override val value: Struct = planID.trigger.value

    override fun applySubstitution(substitution: Substitution) =
        TrackPlanExecutionImpl(
            planID.copy(
                trigger = planID.trigger.copy(
                    value = planID.trigger.value.apply(substitution).castToStruct(),
                ),
            ),
        )

    override fun toString(): String = "TrackPlanExecution(${planID.trigger.value})"

    override fun copy(value: Struct): Goal = TrackPlanExecutionImpl(
        planID.copy(trigger = planID.trigger.copy(value)),
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackPlanExecutionImpl

        if (planID != other.planID) return false
        if (value != other.value) return false

        return true
    }

    override fun hashCode(): Int {
        var result = planID.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
}
