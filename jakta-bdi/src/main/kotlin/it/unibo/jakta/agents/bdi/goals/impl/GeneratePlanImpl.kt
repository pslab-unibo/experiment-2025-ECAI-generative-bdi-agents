package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

class GeneratePlanImpl(
    override val value: Struct,
) : GeneratePlan {
    override fun applySubstitution(substitution: Substitution) =
        GeneratePlanImpl(value.apply(substitution).castToStruct())

    override fun toString(): String = "Generate($value)"

    override fun copy(value: Struct): Goal = GeneratePlanImpl(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GeneratePlanImpl

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
