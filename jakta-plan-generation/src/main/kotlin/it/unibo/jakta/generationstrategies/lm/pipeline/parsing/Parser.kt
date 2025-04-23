package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.ParserImpl
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult

interface Parser {
    fun parse(input: String): ParserResult

    companion object {
        fun of(): Parser = ParserImpl()
    }
}
