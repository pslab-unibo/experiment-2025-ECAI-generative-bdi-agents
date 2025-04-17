package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.PlanFormatterImpl
import it.unibo.tuprolog.core.TermFormatter

interface PlanFormatter {
    val termFormatter: TermFormatter
    val goalFormatter: GoalFormatter
    val triggerFormatter: TriggerFormatter

    fun formatPlan(plan: Plan): String

    fun formatLiteratePlan(litPlan: LiteratePlan): String

    companion object {
        fun of(
            termFormatter: TermFormatter = formatter,
            goalFormatter: GoalFormatter = GoalFormatter.of(termFormatter),
            triggerFormatter: TriggerFormatter = TriggerFormatter.of(termFormatter),
        ): PlanFormatter =
            PlanFormatterImpl(termFormatter, goalFormatter, triggerFormatter)
    }
}
