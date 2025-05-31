package it.unibo.jakta.agents.bdi.engine.messages

import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Message")
data class Message(
    val from: String,
    val type: MessageType,
    val value: SerializableStruct,
)
