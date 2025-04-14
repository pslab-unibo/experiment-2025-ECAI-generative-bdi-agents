package it.unibo.jakta.agents.bdi.parsing.templates.impl

import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.jakta.agents.bdi.parsing.templates.TemplateBuilder

data class PredicateTemplate(
    override val template: String,
    override val templateBuilder: TemplateBuilder = TemplateBuilder(),
) : LiteratePrologTemplate {
    companion object {
        fun of(input: String): PredicateTemplate = PredicateTemplate(input)
    }
}
