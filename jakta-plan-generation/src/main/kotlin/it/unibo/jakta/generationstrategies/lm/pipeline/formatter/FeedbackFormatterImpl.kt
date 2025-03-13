package it.unibo.jakta.generationstrategies.lm.pipeline.formatter

import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.LiteratePrologParser.wrapWithDelimiters
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.feedback.GenerationFeedback
import it.unibo.jakta.agents.bdi.plans.feedback.GoalFeedback
import it.unibo.jakta.agents.bdi.plans.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.plans.feedback.PlanFeedback
import it.unibo.jakta.agents.bdi.plans.feedback.StringFeedback
import it.unibo.tuprolog.core.TermFormatter

class FeedbackFormatterImpl(
    override val termFormatter: TermFormatter,
    override val goalFormatter: GoalFormatter,
    override val triggerFormatter: TriggerFormatter,
) : FeedbackFormatter {
    override fun format(feedback: GenerationFeedback): String =
        when (feedback) {
            is GoalFeedback -> formatGoals(feedback.goals)
            is PlanFeedback -> formatPlans(feedback.plans)
            is StringFeedback -> feedback.message
        }

    fun formatGoals(goals: List<Goal>): String {
        return if (goals.isEmpty()) {
            ""
        } else {
            """Done!
            ${goalFormatter.format(goals, pastTense = true, keepTriggerType = false).joinToString("\n")}"""
        }
    }

    fun formatPlans(plans: List<PlanApplicabilityResult>): String {
        return plans
            .filter { it.error == null && it.trigger != null && it.guards != null }
            .joinToString("\n") {
                StringBuilder().apply {
                    appendLine("${triggerFormatter.format(it.trigger!!)} cannot be chosen because:")
                    it.guards
                        ?.filterNot { it.value }
                        ?.forEach {
                            appendLine(
                                "${termFormatter.format(it.key.removeSource()).wrapWithDelimiters()} is false",
                            )
                        }

                    val trueGuards = it.guards?.filter { it.value }
                    if (trueGuards != null && trueGuards.isNotEmpty()) {
                        appendLine()
                        appendLine("That said:")
                        trueGuards.forEach {
                            appendLine(
                                "${termFormatter.format(it.key.removeSource()).wrapWithDelimiters()} is true",
                            )
                        }
                    }
                }.toString()
            }
    }
}
