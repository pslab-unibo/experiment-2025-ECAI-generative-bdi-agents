package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PlanApplicabilityResult")
data class PlanApplicabilityResult(
    val trigger: Trigger? = null,
    val guards: Map<SerializableStruct, Boolean>? = null,
    val error: String? = null,
)
