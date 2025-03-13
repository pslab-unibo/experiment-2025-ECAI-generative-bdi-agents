package it.unibo.jakta.generationstrategies.lm.pipeline.formatter

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.plans.feedback.GenerationFeedback
import it.unibo.tuprolog.core.TermFormatter

interface FeedbackFormatter {
    val termFormatter: TermFormatter
    val goalFormatter: GoalFormatter
    val triggerFormatter: TriggerFormatter

    fun format(feedback: GenerationFeedback): String

    companion object {
        fun of(
            termFormatter: TermFormatter = formatter,
            goalFormatter: GoalFormatter = GoalFormatter.of(termFormatter),
            triggerFormatter: TriggerFormatter = TriggerFormatter.of(termFormatter),
        ) = FeedbackFormatterImpl(termFormatter, goalFormatter, triggerFormatter)
    }
}
