package it.unibo.jakta.generationstrategies.lm.pipeline

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.tuprolog.core.Struct

interface ResponseParser {
    fun parseStruct(response: String): Struct?

    fun parseGoal(response: String): Goal?

    companion object {
        fun of() = ResponseParserImpl()
    }
}
