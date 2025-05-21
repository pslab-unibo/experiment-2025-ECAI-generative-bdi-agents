package it.unibo.jakta.agents.bdi.engine.goals.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.goals.AddBelief
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AddBelief")
internal class AddBeliefImpl(
    private val addedBelief: Belief,
) : AddBelief {
    override val value: SerializableStruct get() = addedBelief.rule.head

    override fun applySubstitution(substitution: Substitution) =
        AddBeliefImpl(addedBelief.applySubstitution(substitution))

    override fun toString(): String = "AddBelief($value)"

    override fun copy(value: Struct): Goal = AddBeliefImpl(Belief.from(value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AddBelief
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int = value.hashCode()
}
