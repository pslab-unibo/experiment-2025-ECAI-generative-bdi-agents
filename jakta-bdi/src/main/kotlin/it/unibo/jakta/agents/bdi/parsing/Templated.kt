package it.unibo.jakta.agents.bdi.parsing

import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate.Companion.defaultStopWords

interface Templated {
    val template: LiteratePrologTemplate?
    val slotValues: List<Pair<String, String>>

    fun verbalize(): String =
        template?.buildGroundTerm(slotValues, defaultStopWords) ?: this.toString()
}
