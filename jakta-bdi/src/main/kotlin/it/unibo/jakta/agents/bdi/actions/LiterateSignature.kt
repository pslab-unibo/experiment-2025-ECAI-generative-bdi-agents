package it.unibo.jakta.agents.bdi.actions

import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleProgram
import it.unibo.jakta.agents.bdi.parsing.templates.impl.GoalTemplate
import it.unibo.tuprolog.solve.Signature

data class LiterateSignature(
    val signature: Signature,
    val description: String = "",
) {
    val template: GoalTemplate = GoalTemplate.of(
        tangleProgram(signature.name).firstOrNull() ?: signature.name,
    )
    val name: String = template.struct?.functor ?: signature.name
    val arity: Int = signature.arity
}
