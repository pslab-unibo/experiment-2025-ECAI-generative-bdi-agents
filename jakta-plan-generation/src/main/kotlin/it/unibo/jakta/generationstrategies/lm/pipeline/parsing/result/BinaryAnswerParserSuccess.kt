package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.BinaryAnswerParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.ParserSuccess

sealed interface BinaryAnswerParserSuccess : ParserSuccess, BinaryAnswerParserResult {
    data class AffirmativeResponse(
        override val rawContent: String,
    ) : BinaryAnswerParserSuccess

    data class NegativeResponse(
        override val rawContent: String,
    ) : BinaryAnswerParserSuccess
}
