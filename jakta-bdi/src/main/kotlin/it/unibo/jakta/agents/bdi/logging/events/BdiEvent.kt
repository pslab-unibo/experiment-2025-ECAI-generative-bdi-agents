package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger

sealed interface BdiEvent : LogEvent {
    data class EventSelected(
        val event: Event,
    ) : BdiEvent {
        override val description: String =
            "Selected ${eventType(event)} event ${triggerDescription(event.trigger)}"

        override val metadata: Map<String, Any?> = buildMap {
            "intention" to event.intention?.id?.id
        }
    }

    companion object {
        fun triggerDescription(triggerType: Trigger): String =
            "${triggerType.javaClass.simpleName} with value ${termFormatter.format(triggerType.value)}"

        fun eventType(e: Event) = if (e.isExternal()) {
            "external"
        } else {
            "internal"
        }
    }
}
