package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.messages.Message

sealed interface MessageEvent : LogEvent {
    data class NewMessage(
        val message: Message,
    ) : MessageEvent {
        val messageContent = termFormatter.format(message.value)

        override val description =
            "New message with speech act ${message.type.javaClass.simpleName} from ${message.from}: $messageContent"
    }
}
