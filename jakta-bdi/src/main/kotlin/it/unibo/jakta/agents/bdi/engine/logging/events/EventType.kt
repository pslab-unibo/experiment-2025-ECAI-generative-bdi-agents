package it.unibo.jakta.agents.bdi.engine.logging.events

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class EventType(
    val type: String,
)
