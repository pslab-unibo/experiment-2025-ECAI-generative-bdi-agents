package it.unibo.jakta.agents.bdi.generationstrategies

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.generated.GeneratedPlan
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesReader
import java.io.StringReader

interface GenerationStrategy {
    val planLibrary: PlanLibrary?
    val actions: List<Action<*, *, *>>

    fun generatePlan(plan: GeneratedPlan): PlanGenerationResult?

    fun copy(
        plans: PlanLibrary? = this.planLibrary,
        actions: List<Action<*, *, *>> = this.actions,
    ): GenerationStrategy

    companion object {
        fun oneShot(): GenerationStrategy = OneShotGenerationStrategy()

        fun loadTheoryFromString(program: String): Theory =
            ClausesReader.withDefaultOperators().readTheory(StringReader(program))
    }
}
