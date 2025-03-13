package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.logging.events.BdiEvent.Companion.eventType
import it.unibo.jakta.agents.bdi.logging.events.BdiEvent.Companion.triggerDescription

sealed interface BdiEvent : LogEvent {
    companion object {
        fun triggerDescription(triggerType: Trigger): String =
            "${triggerType.javaClass.simpleName} with value ${formatter.format(triggerType.value)}"

        fun eventType(e: Event) = if (e.isExternal()) {
            "external"
        } else {
            "internal"
        }
    }
}

data class EventSelected(
    val event: Event,
) : BdiEvent {
    override val description: String =
        "Selected ${eventType(event)} event ${triggerDescription(event.trigger)}"

    override val params: Map<String, Any?> = buildMap {
        "intention" to event.intention?.id?.id
    }
}
