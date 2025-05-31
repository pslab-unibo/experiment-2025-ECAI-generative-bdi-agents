package it.unibo.jakta.agents.bdi.engine.logging.events

import it.unibo.jakta.agents.bdi.engine.actions.Action
import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface ActionEvent : AgentEvent {
    @Serializable
    @SerialName("ActionAddition")
    data class ActionAddition(
        @Transient
        val action: Action<*, *, *>? = null,
        val actionType: String,
        val actionName: String,
        override val description: String?,
    ) : ActionEvent {
        constructor(action: Action<*, *, *>) : this(
            action,
            actionType(action),
            action.signature.name,
            "Added ${actionType(action)} action: ${action.signature.name}",
        )
    }

    companion object {
        fun actionType(action: Action<*, *, *>): String =
            when (action) {
                is InternalAction -> "internal"
                is ExternalAction -> "external"
                else -> ""
            }
    }
}
