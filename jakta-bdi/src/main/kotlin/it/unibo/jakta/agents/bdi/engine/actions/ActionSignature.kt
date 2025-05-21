package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableSignature
import it.unibo.tuprolog.solve.Signature
import kotlinx.serialization.Serializable

@Serializable
data class ActionSignature(
    val signature: SerializableSignature,
    val parameterNames: List<String>,
) {
    companion object {
        fun of(
            name: String,
            arity: Int,
            parameterNames: List<String>,
        ): ActionSignature {
            require(parameterNames.size == arity) {
                "Number of parameter names (${parameterNames.size}) must match arity ($arity)"
            }
            return ActionSignature(Signature(name, arity), parameterNames)
        }
    }

    val name: String get() = signature.name
    val arity: Int get() = signature.arity

    override fun toString(): String = "$name(${parameterNames.joinToString(", ")})"
}
