package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.logging.events.ActionEvent.Companion.actionType

sealed interface ActionEvent : LogEvent {
    companion object {
        fun actionType(action: Action<*, *, *>): String =
            when (action) {
                is InternalAction -> "Internal"
                is ExternalAction -> "External"
                else -> ""
            }
    }
}

data class ActionAddition(
    val action: Action<*, *, *>,
) : ActionEvent {
    val actionType = actionType(action)
    val actionName = action.signature.name

    override val description =
        "$actionType action `$actionName` added"

    override val params = super.params + buildMap {
        put("name", actionName)
        put("type", actionType)
    }
}

data class ActionTriggered(
    val action: Action<*, *, *>,
) : ActionEvent {
    override val description =
        "${actionType(action)} action `${action.signature.name}` triggered"
}

data class ActionFinished(
    val action: Action<*, *, *>,
) : ActionEvent {
    override val description =
        "${actionType(action)} action `${action.signature.name}` finished"
}
