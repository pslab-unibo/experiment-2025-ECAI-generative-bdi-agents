package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.BinaryResponseParserImpl
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult

interface BynaryResponseParser : ResponseParser {
    override fun buildResult(): ParserResult

    companion object {
        fun of() = BinaryResponseParserImpl()
    }
}
