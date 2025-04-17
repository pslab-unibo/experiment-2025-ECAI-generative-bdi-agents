package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsedStatement

interface TagProcessor {
    fun process(content: String): Boolean

    fun getItems(): List<ParsedStatement>
}
