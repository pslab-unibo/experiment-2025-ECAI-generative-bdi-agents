package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result

sealed interface ParserFailure : ParserResult {
    data class GenericParserFailure(
        override val rawContent: String,
    ) : ParserFailure

    data class EmptyResponse(
        override val rawContent: String,
    ) : ParserSuccess

    data class NetworkRequestFailure(
        override val rawContent: String,
    ) : ParserSuccess
}
