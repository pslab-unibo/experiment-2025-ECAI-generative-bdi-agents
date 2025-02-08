package it.unibo.jakta.agents.bdi.logging

import it.unibo.jakta.agents.bdi.messages.Message

sealed interface MessageEvent : LogEvent

data class NewMessage(
    val message: Message,
) : MessageEvent {
    override val description =
        "New message with speech act ${message.type.javaClass.simpleName} from ${message.from}: ${message.value}"
}
