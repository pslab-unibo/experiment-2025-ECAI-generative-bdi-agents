package it.unibo.jakta.agents.bdi.goals.impl

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution

internal class SpawnImpl(override val goal: Goal) : Spawn {
    override val value: Struct get() = goal.value

    override fun applySubstitution(substitution: Substitution) =
        SpawnImpl(goal.applySubstitution(substitution))

    override fun toString() = "Spawn($value)"

    override fun copy(value: Struct): Goal = SpawnImpl(goal.copy(value = value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Spawn
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int = value.hashCode()
}
