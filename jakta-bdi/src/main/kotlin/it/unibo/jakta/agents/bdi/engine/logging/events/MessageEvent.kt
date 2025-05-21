package it.unibo.jakta.agents.bdi.engine.logging.events

import it.unibo.jakta.agents.bdi.engine.messages.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("MessageEvent")
sealed interface MessageEvent : JaktaLogEvent {
    @Serializable
    @SerialName("NewMessage")
    data class NewMessage(
        val message: Message,
        override val description: String,
    ) : MessageEvent {
        constructor(message: Message) : this(message, "Received message $message")
    }
}
