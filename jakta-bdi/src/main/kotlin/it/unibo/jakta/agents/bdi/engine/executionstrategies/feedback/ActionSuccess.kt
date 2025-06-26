package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.engine.actions.ActionSignature
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableTerm
import it.unibo.tuprolog.core.Term
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface ActionSuccess : GoalSuccess {
    val actionSignature: ActionSignature
    val providedArguments: List<SerializableTerm>

    @Serializable
    @SerialName("GenericActionSuccess")
    data class GenericActionSuccess(
        override val actionSignature: ActionSignature,
        override val providedArguments: List<SerializableTerm>,
        override val description: String?,
    ) : ActionSuccess {
        constructor(actionSignature: ActionSignature, providedArguments: List<Term>) : this(
            actionSignature,
            providedArguments,
            "The action \"${actionSignature.name}\" was successfully executed with the given arguments: " +
                providedArguments.joinToString(", ") { termFormatter.format(it) },
        )
    }
}
