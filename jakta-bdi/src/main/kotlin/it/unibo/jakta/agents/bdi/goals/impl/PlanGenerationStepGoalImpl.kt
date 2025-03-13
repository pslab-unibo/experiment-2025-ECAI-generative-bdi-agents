package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.PlanGenerationStepGoal
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

internal class PlanGenerationStepGoalImpl(
    override val planID: PlanID,
    override val goal: Goal,
) : PlanGenerationStepGoal {
    override val value: Struct
        get() = goal.value

    override fun applySubstitution(substitution: Substitution) =
        PlanGenerationStepGoalImpl(planID, goal.applySubstitution(substitution))

    override fun toString(): String = "Generate($goal)"

    override fun copy(value: Struct): Goal = PlanGenerationStepGoalImpl(planID, goal.copy(value = value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as PlanGenerationStepGoalImpl
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int = value.hashCode()
}
