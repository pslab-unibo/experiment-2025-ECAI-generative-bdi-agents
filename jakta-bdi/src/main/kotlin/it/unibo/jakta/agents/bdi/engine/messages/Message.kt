package it.unibo.jakta.agents.bdi.engine.messages

import it.unibo.tuprolog.core.Struct
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val from: String,
    val type: MessageType,
    val value: Struct,
)
