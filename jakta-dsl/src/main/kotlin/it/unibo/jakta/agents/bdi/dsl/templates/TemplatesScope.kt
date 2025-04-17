package it.unibo.jakta.agents.bdi.dsl.templates

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleProgram
import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.jakta.agents.bdi.parsing.templates.impl.PredicateTemplate
import it.unibo.jakta.agents.bdi.parsing.templates.impl.TypeTemplate

class TemplatesScope : Builder<List<LiteratePrologTemplate>> {
    var templates = mutableListOf<LiteratePrologTemplate>()

    fun type(atom: String) {
        val fragment = tangleProgram(atom).firstOrNull() ?: atom
        templates += TypeTemplate.of(fragment)
    }

    fun predicate(atom: String) {
        val fragment = tangleProgram(atom).firstOrNull() ?: atom
        templates += PredicateTemplate.of(fragment)
    }

    override fun build(): List<LiteratePrologTemplate> = templates
}
