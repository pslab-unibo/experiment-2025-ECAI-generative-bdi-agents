package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

sealed interface ParsedStatement {
    val rawContent: String

    data class SimpleStatement(
        override val rawContent: String,
        val parsedContent: String,
    ) : ParsedStatement

    data class PlanStatement(
        override val rawContent: String,
        val trigger: String,
        val guards: List<String>,
        val goals: List<String>,
    ) : ParsedStatement
}
