package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction

sealed interface ActionEvent : LogEvent {
    data class ActionAddition(
        val action: Action<*, *, *>,
    ) : ActionEvent {
        val actionType = actionType(action)
        val actionName = action.signature.name

        override val description =
            "Added $actionType action $actionName"

        override val metadata = super.metadata + buildMap {
            put("actionName", actionName)
            put("actionType", actionType)
        }
    }

    companion object {
        fun actionType(action: Action<*, *, *>): String =
            when (action) {
                is InternalAction -> "Internal"
                is ExternalAction -> "External"
                else -> ""
            }
    }
}
