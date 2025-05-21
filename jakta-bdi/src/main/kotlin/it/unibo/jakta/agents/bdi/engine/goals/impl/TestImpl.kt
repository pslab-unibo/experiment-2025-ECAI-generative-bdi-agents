package it.unibo.jakta.agents.bdi.engine.goals.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.goals.Test
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Test")
internal class TestImpl(
    override val belief: Belief,
) : Test {
    override val value: SerializableStruct get() = belief.rule.head

    override fun applySubstitution(substitution: Substitution) = TestImpl(belief.applySubstitution(substitution))

    override fun toString(): String = "Test($value)"

    override fun copy(value: Struct): Goal = TestImpl(Belief.from(value))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Test
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int = value.hashCode()
}
