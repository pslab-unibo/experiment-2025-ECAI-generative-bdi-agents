package it.unibo.jakta.agents.bdi.engine.beliefs.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableRule
import it.unibo.tuprolog.core.Rule
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AdmissibleBelief")
internal class AdmissibleBeliefImpl(
    override val rule: SerializableRule,
    override val purpose: String? = null,
) : AbstractBelief(),
    AdmissibleBelief {
    override fun copy(
        rule: Rule,
        purpose: String?,
    ): AdmissibleBelief = AdmissibleBeliefImpl(rule, purpose)
}
