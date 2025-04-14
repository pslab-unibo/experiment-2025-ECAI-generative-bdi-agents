package it.unibo.jakta.agents.bdi.parsing.templates.impl

import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.jakta.agents.bdi.parsing.templates.TemplateBuilder

data class GoalTemplate(
    override val template: String,
    override val templateBuilder: TemplateBuilder,
) : LiteratePrologTemplate {
    companion object {
        fun of(
            template: String,
            templateBuilder: TemplateBuilder = TemplateBuilder(),
        ): GoalTemplate = GoalTemplate(template, templateBuilder)
    }
}
