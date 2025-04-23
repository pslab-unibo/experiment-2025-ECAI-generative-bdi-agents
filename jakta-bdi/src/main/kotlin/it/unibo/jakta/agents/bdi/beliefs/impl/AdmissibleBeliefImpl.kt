package it.unibo.jakta.agents.bdi.beliefs.impl

import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.tuprolog.core.Rule

internal class AdmissibleBeliefImpl(
    override val rule: Rule,
    override val purpose: String?,
) : BaseBelief(), AdmissibleBelief {
    override fun copy(rule: Rule, purpose: String?): AdmissibleBelief =
        AdmissibleBeliefImpl(rule, purpose)
}
