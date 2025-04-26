package it.unibo.jakta.playground

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserSuccess

fun main() {
    val parser = Parser.of()
    val res = parser.parse(text6)
    if (res is ParserSuccess.NewResult) {
        println(res.plans.joinToString("\n"))
        println(res.admissibleGoals.joinToString("\n"))
        println(res.admissibleBeliefs.joinToString("\n"))
    }
}
