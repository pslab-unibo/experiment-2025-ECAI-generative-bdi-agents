package it.unibo.jakta.generationstrategies.lm.pipeline

import it.unibo.jakta.agents.bdi.LiteratePrologParser.tanglePlanBody
import it.unibo.jakta.agents.bdi.LiteratePrologParser.tangleStruct
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.tuprolog.core.Struct

class ResponseParserImpl : ResponseParser {
    override fun parseStruct(response: String): Struct? =
        tangleStruct(response)

    override fun parseGoal(response: String): Goal? {
        val parsedGoals = tanglePlanBody(response)
        return if (parsedGoals.isEmpty()) null else parsedGoals.first()
    }
}
