package it.unibo.jakta.agents.bdi.engine.events.impl

import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Event")
internal data class EventImpl(
    override val trigger: Trigger,
    override val intention: Intention?,
) : Event
