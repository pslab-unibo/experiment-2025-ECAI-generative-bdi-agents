package it.unibo.jakta.agents.bdi.logging

import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.logging.BdiEvent.Companion.eventDescription

sealed interface BdiEvent : LogEvent {
    companion object {
        fun eventDescription(evt: Event, state: String): String = if (evt.isExternal()) {
            "External event of type ${triggerDescription(evt.trigger)}" +
                if (state.isNotEmpty()) " $state" else ""
        } else {
            "Internal event of type ${triggerDescription(evt.trigger)}" +
                if (state.isNotEmpty()) {
                    " $state"
                } else {
                    "" +
                        "\n\twith intention ${evt.intention?.id?.id}"
                }
        }

        fun triggerDescription(triggerType: Trigger): String =
            "${triggerType.javaClass.simpleName} with value ${triggerType.value}"
    }
}

data class EventSelected(
    val event: Event,
) : BdiEvent {
    override val description: String = eventDescription(event, "selected")
}
