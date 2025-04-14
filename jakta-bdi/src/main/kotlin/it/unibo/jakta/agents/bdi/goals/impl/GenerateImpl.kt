package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

class GenerateImpl(
    override val value: Struct,
    override val literateValue: String,
) : Generate {
    override fun applySubstitution(substitution: Substitution) =
        GenerateImpl(value.apply(substitution).castToStruct(), literateValue)

    override fun toString(): String = "Generate($literateValue)"

    override fun copy(value: Struct): Goal = GenerateImpl(value, literateValue)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GenerateImpl

        return value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
