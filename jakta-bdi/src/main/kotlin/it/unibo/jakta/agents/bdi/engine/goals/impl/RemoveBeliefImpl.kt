package it.unibo.jakta.agents.bdi.engine.goals.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("RemoveBelief")
internal class RemoveBeliefImpl(
    private val removedBelief: Belief,
) : RemoveBelief {
    override val value: SerializableStruct get() = removedBelief.rule.head

    override fun applySubstitution(substitution: Substitution) =
        RemoveBeliefImpl(removedBelief.applySubstitution(substitution))

    override fun toString(): String = "RemoveBelief($value)"

    override fun copy(value: Struct): Goal = RemoveBeliefImpl(Belief.from(value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RemoveBelief
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int = value.hashCode()
}
