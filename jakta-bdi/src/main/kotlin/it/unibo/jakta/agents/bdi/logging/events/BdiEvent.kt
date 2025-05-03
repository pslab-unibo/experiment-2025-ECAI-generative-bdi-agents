package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter

sealed interface BdiEvent : LogEvent {
    data class EventSelected(
        val event: Event,
    ) : BdiEvent {
        private val trigger = triggerFormatter.format(event.trigger)

        override val description: String =
            "Selected ${eventType(event)} event $trigger"

        override val metadata: Map<String, Any?> = super.metadata + buildMap {
            "intentionId" to event.intention?.id?.id
        }
    }

    companion object {
        fun eventType(e: Event) = if (e.isExternal()) {
            "external"
        } else {
            "internal"
        }
    }
}
