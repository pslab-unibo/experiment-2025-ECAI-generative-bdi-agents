package it.unibo.jakta.generationstrategies.lm.pipeline.formatter

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.tuprolog.core.TermFormatter

interface TriggerFormatter {
    fun format(trigger: Trigger): String

    companion object {
        fun of(termFormatter: TermFormatter = formatter) =
            TriggerFormatterImpl(termFormatter)
    }
}
