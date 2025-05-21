package it.unibo.jakta.agents.bdi.engine.goals.impl

import it.unibo.jakta.agents.bdi.engine.goals.ActInternally
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ActInternally")
internal class ActInternallyImpl(
    override val value: SerializableStruct,
) : ActInternally {
    override fun applySubstitution(substitution: Substitution) =
        ActInternallyImpl(value.apply(substitution).castToStruct())

    override fun toString(): String = "ActInternally($value)"

    override fun copy(value: Struct) = ActInternallyImpl(value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ActInternally
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int = value.hashCode()
}
