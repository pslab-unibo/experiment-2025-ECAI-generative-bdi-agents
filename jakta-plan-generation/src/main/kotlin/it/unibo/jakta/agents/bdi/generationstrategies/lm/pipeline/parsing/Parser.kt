package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.impl.ParserImpl
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserResult

interface Parser {
    fun parse(input: String): ParserResult

    companion object {
        fun of(): Parser = ParserImpl()
    }
}
