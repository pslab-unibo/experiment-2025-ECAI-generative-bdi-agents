package it.unibo.jakta.agents.bdi.engine.perception.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.perception.Perception

internal data class PerceptionImpl(
    val beliefs: Iterable<Belief> = emptyList(),
) : Perception {
    override fun percept(): BeliefBase = BeliefBase.of(beliefs = beliefs)
}
