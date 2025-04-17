package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.PlanStreamingParserImpl

interface PlanStreamingParser : Parser {

    fun getParsedPlan(): ParsedPlan?

    data class ParsedPlan(
        val trigger: String,
        val guards: List<String>,
        val goals: List<String>,
    )

    companion object {
        fun of() = PlanStreamingParserImpl()
    }
}
