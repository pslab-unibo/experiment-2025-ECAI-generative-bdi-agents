package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.PromptFormatterImpl
import it.unibo.tuprolog.core.TermFormatter

interface PromptFormatter {
    val termFormatter: TermFormatter
    val planFormatter: PlanFormatter

    fun formatPlans(plans: List<Plan>): String

    fun formatBeliefs(beliefs: BeliefBase): String

    fun formatActions(actions: List<Action<*, *, *>>): String

    companion object {
        fun of(termFormatter: TermFormatter = formatter): PromptFormatter {
            val goalFormatter = GoalFormatter.of(termFormatter)
            val planFormatter = PlanFormatter.of(termFormatter, goalFormatter)
            return PromptFormatterImpl(termFormatter, planFormatter)
        }
    }
}
