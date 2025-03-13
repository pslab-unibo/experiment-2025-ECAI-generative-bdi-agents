package it.unibo.jakta.generationstrategies.lm.pipeline.formatter

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.pipeline.formatter.PromptFormatterImpl.Companion.are
import it.unibo.jakta.generationstrategies.lm.pipeline.formatter.PromptFormatterImpl.Companion.format
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter

class PlanFormatterImpl(
    override val termFormatter: TermFormatter,
    override val goalFormatter: GoalFormatter,
    override val triggerFormatter: TriggerFormatter,
) : PlanFormatter {

    private fun formatTrigger(trigger: Trigger): String = triggerFormatter.format(trigger)

    private fun formatGuard(guard: Struct): String =
        if (guard.isTruth) {
            ""
        } else {
            guard.accept(GuardVisitor())
        }

    private fun formatGoals(goals: List<Goal>): String {
        val planBody = goalFormatter.format(goals).joinToString("\nand ")
        return if (planBody.isBlank()) "nothing happens" else planBody
    }

    override fun formatPlan(plan: Plan): String {
        var trigger = formatTrigger(plan.trigger)
        var guard = formatGuard(plan.guard)
        var body = formatGoals(plan.goals)

        return if (guard.isBlank()) {
            """- when ${format(trigger)} is chosen
                then ${format(body)}
            """
        } else {
            """- ${format(trigger)} can be chosen
                only if $guard ${are(plan.guard.args)} true
                then ${format(body)}
            """
        }
    }

    override fun formatLiteratePlan(litPlan: LiteratePlan): String {
        var trigger = litPlan.literateTrigger?.trimIndent()
        var guard = litPlan.literateGuard?.trimIndent()
        var body = litPlan.literateGoals?.trimIndent()

        return """```
                Goal: $trigger
                
                Preconditions:
                
                $guard
                
                Steps:
                
                $body
                ```
            """
    }
}
