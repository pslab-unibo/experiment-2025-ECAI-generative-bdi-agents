package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plangeneration.GenerationConfig
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

class GeneratePlanImpl(
    override val goal: Goal,
    override val generationConfig: GenerationConfig?,
) : GeneratePlan {
    override val value: Struct get() = goal.value

    override fun applySubstitution(substitution: Substitution) =
        GeneratePlanImpl(goal.applySubstitution(substitution), generationConfig)

    override fun toString(): String = "Generate($value)"

    override fun copy(value: Struct): Goal =
        GeneratePlanImpl(goal.copy(value = value), generationConfig)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as GeneratePlanImpl
        return value == other.value
    }

    override fun hashCode(): Int = value.hashCode()
}
