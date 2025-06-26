package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.impl.ParserImpl
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestResult

fun interface Parser {
    fun parse(input: String): RequestResult

    companion object {
        fun create(): Parser = ParserImpl()
    }
}
