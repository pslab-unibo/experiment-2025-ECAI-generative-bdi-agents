package it.unibo.jakta.agents.bdi.engine.plans

import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Truth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PlanID")
data class PlanID(
    val trigger: Trigger,
    val guard: SerializableStruct = Truth.TRUE,
)
