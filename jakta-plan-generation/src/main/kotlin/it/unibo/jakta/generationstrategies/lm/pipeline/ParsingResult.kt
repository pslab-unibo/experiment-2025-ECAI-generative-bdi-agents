package it.unibo.jakta.generationstrategies.lm.pipeline

sealed interface ParsingResult {
    val content: String
    val fragmentToParse: String?
}

data class ParserSuccess(override val content: String, override val fragmentToParse: String) : ParsingResult
data class ParserStop(override val content: String, override val fragmentToParse: String? = null) : ParsingResult
data class ParserFail(override val content: String, override val fragmentToParse: String? = null) : ParsingResult
