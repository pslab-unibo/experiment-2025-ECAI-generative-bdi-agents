package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger

sealed interface BdiEvent : LogEvent {
    companion object {
        fun eventDescription(evt: Event, state: String): String = if (evt.isExternal()) {
            if (state.isNotEmpty()) {
                "$state "
            } else {
                ""
            } + "external event of type ${triggerDescription(evt.trigger)}"
        } else {
            if (state.isNotEmpty()) {
                "$state "
            } else {
                ""
            } + "internal event of type ${triggerDescription(evt.trigger)}" +
                if (state.isEmpty()) {
                    ""
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
    override val description: String = BdiEvent.Companion.eventDescription(event, "Selected")
}
