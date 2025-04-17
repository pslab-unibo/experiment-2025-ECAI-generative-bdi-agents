package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.BinaryAnswerParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.ParserFailure

sealed interface BinaryAnswerParserFailure : ParserFailure, BinaryAnswerParserResult {
    data class InvalidResponse(override val rawContent: String) : BinaryAnswerParserFailure
}
