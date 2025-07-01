package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result

import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserResult

sealed interface RequestSuccess : RequestResult {
    val chatCompletionId: String

    data class NewRequestResult(
        override val chatCompletionId: String,
        val parserResult: ParserResult,
    ) : RequestSuccess
}
