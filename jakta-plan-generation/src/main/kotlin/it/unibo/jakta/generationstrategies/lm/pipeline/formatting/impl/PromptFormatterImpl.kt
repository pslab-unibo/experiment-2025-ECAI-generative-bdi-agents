package it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl

import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.Jakta.wrapWithDelimiters
import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PlanFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptFormatter
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Truth

class PromptFormatterImpl(
    override val termFormatter: TermFormatter,
    override val planFormatter: PlanFormatter,
) : PromptFormatter {

    override fun formatPlans(plans: List<Plan>): String =
        plans.joinToString("\n") {
            if (it is LiteratePlan) {
                planFormatter.formatLiteratePlan(it)
            } else {
                planFormatter.formatPlan(it)
            }
        }

    override fun formatBeliefs(beliefs: BeliefBase): String =
        beliefs.joinToString("\n") {
            ""
            if (it.rule.body != Truth.TRUE) {
                "- ${termFormatter.format(it.rule.head.removeSource()).wrapWithDelimiters()} is ${it.rule.body}"
            } else {
                "- ${termFormatter.format(it.rule.head.removeSource()).wrapWithDelimiters()}"
            }
        }

    override fun formatActions(actions: List<Action<*, *, *>>): String =
        actions.joinToString("\n") { "- ${it.signature.description.trimIndent()}" }

    companion object {
        val haveBeen: (List<Term>) -> String = { g -> if (g.size > 1) "have been" else "has been" }
        val are: (List<Term>) -> String = { g -> if (g.size > 1) "are" else "is" }
        val became: (List<Term>) -> String = { g -> "became" }
        val become: (List<Term>) -> String = { g -> if (g.size > 1) "become" else "becomes" }

        fun format(input: String): String {
            val regex = Regex("\\b[A-Z][A-Za-z]*\\b")
            return regex.replace(input) { matchResult ->
                "@${matchResult.value.lowercase()}"
            }
        }

        fun List<Term>.formatWithAnd(formatter: TermFormatter): String =
            this.joinToString(", ") { formatter.format(it).wrapWithDelimiters() }
                .let { str ->
                    val lastCommaIndex = str.lastIndexOf("`,")
                    if (lastCommaIndex != -1) {
                        str.replaceRange(lastCommaIndex, lastCommaIndex + 2, "` and")
                    } else {
                        str
                    }
                }
    }
}
