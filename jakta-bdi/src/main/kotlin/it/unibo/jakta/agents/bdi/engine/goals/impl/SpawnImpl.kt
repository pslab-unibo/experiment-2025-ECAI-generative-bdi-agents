package it.unibo.jakta.agents.bdi.engine.goals.impl

import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.goals.Spawn
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Spawn")
internal class SpawnImpl(
    override val goal: Goal,
) : Spawn {
    override val value: SerializableStruct get() = goal.value

    override fun applySubstitution(substitution: Substitution) = SpawnImpl(goal.applySubstitution(substitution))

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
