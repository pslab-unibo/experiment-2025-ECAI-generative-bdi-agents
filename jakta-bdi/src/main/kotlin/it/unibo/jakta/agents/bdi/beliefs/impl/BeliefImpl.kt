package it.unibo.jakta.agents.bdi.beliefs.impl

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.tuprolog.core.Rule

internal class BeliefImpl(
    override val rule: Rule,
    override val purpose: String?,
) : BaseBelief() {
    override fun copy(rule: Rule, purpose: String?): Belief = BeliefImpl(rule, purpose)
}
