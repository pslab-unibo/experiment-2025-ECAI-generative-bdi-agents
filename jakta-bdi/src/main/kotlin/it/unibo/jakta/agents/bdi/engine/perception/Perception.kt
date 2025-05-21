package it.unibo.jakta.agents.bdi.engine.perception

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.perception.impl.PerceptionImpl

/** Component of a BDI Agent that let it perceive the environment. */
interface Perception {
    /**
     * Operation done by a BDI Agent at each reasoning cycle iteration.
     * @return a [BeliefBase] that describes the environment where the agent is situated.
     */
    fun percept(): BeliefBase

    companion object {
        fun empty(): Perception = PerceptionImpl()

        fun of(
            belief: Belief,
            vararg beliefs: Belief,
        ): Perception = of(listOf(belief, *beliefs))

        fun of(beliefs: Iterable<Belief>): Perception = PerceptionImpl(beliefs)
    }
}
