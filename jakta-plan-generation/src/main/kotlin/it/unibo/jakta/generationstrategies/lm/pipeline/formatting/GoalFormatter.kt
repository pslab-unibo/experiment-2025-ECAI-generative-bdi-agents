package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.GoalFormatterImpl
import it.unibo.tuprolog.core.TermFormatter

interface GoalFormatter {
    val termFormatter: TermFormatter

    fun format(
        goals: List<Goal>,
        pastTense: Boolean = false,
        keepTriggerType: Boolean = true,
    ): List<String>

    companion object {
        fun of(termFormatter: TermFormatter = formatter) = GoalFormatterImpl(termFormatter)
    }
}
