package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result

sealed interface ParserResult {
    val rawContent: String

    sealed interface ParserFailure : ParserResult

    sealed interface ParserSuccess : ParserResult

    sealed interface BinaryAnswerParserResult : ParserResult

    sealed interface PlanGenerationParserResult : ParserResult
}
