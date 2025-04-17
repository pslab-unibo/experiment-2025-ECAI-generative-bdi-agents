package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult

interface ResponseParser : Parser {
    fun buildResult(): ParserResult

    fun isComplete(): Boolean
}
