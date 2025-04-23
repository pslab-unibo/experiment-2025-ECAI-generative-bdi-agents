package it.unibo.jakta.agents.bdi.actions

import it.unibo.tuprolog.solve.Signature

data class ExtendedSignature(
    val signature: Signature,
    val parameterNames: List<String>,
) {
    companion object {
        fun of(name: String, arity: Int, parameterNames: List<String>): ExtendedSignature {
            require(parameterNames.size == arity) {
                "Number of parameter names (${parameterNames.size}) must match arity ($arity)"
            }
            return ExtendedSignature(Signature(name, arity), parameterNames)
        }
    }

    val name: String get() = signature.name
    val arity: Int get() = signature.arity

    override fun toString(): String =
        "$name(${parameterNames.joinToString(", ")})"
}
