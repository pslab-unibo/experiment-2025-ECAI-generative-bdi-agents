package it.unibo.jakta.agents.bdi.dsl.beliefs

import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.dsl.beliefs.BeliefsScope.Companion.createBelief
import it.unibo.tuprolog.core.Fact

object BeliefMetadata {
    class BeliefContext(val belief: Belief) {
        val functor = belief.rule.head.functor
        val args = belief
            .rule
            .head
            .removeSource()
            .args
            .map { "`${termFormatter.format(it)}`" }
    }

    fun Belief.meaning(block: BeliefContext.() -> String): Belief {
        val context = BeliefContext(this)
        val purpose = context.block()
        return this.copy(purpose = purpose)
    }

    fun Fact.meaning(block: BeliefContext.() -> String): Belief {
        val belief = createBelief(this.head)
        val context = BeliefContext(belief)
        val purpose = context.block()
        return belief.copy(purpose = purpose)
    }
}
