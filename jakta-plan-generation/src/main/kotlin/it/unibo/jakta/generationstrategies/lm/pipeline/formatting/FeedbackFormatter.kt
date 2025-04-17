package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ExecutionFeedback
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.FeedbackFormatterImpl
import it.unibo.tuprolog.core.TermFormatter

interface FeedbackFormatter {
    val planFormatter: PlanFormatter

    fun format(feedback: ExecutionFeedback): String

    companion object {
        fun of(
            termFormatter: TermFormatter = formatter,
            goalFormatter: GoalFormatter = GoalFormatter.of(termFormatter),
            triggerFormatter: TriggerFormatter = TriggerFormatter.of(termFormatter),
        ): FeedbackFormatter {
            val planFormatter = PlanFormatter.of(termFormatter, goalFormatter, triggerFormatter)
            return FeedbackFormatterImpl(planFormatter)
        }
    }
}
