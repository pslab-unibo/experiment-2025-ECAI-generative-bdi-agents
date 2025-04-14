package it.unibo.jakta.agents.bdi.actions

import it.unibo.tuprolog.solve.Signature

data class LiterateSignature(
    val signature: Signature,
    val description: String = "",
) {
    val name: String = signature.name.trimIndent()
    val arity: Int = signature.arity
}
