package it.unibo.jakta.agents.bdi.engine.goals.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.goals.UpdateBelief
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("UpdateBelief")
internal class UpdateBeliefImpl(
    private val updatedBelief: Belief,
) : UpdateBelief {
    override val value: SerializableStruct get() = updatedBelief.rule.head

    override fun applySubstitution(substitution: Substitution) =
        UpdateBeliefImpl(updatedBelief.applySubstitution(substitution))

    override fun toString(): String = "UpdateBelief($updatedBelief)"

    override fun copy(value: Struct): Goal = UpdateBeliefImpl(Belief.from(value)) // TODO("QUI SI ROMPE")
}
