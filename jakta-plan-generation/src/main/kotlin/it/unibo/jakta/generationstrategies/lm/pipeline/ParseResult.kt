package it.unibo.jakta.generationstrategies.lm.pipeline

sealed interface ParseResult {
    val content: String
    val fragmentToParse: String?
}

data class ParserResultImpl(override val content: String, override val fragmentToParse: String) : ParseResult
