package it.unibo.jakta.agents.bdi.engine.beliefs.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableRule
import it.unibo.tuprolog.core.Rule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Belief")
internal class BeliefImpl(
    override val rule: SerializableRule,
    override val purpose: String? = null,
) : AbstractBelief() {
    override fun copy(
        rule: Rule,
        purpose: String?,
    ): Belief = BeliefImpl(rule, purpose)
}
