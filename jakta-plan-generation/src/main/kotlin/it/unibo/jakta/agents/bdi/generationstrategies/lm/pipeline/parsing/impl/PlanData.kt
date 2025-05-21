package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.impl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PlanData(
    @SerialName("EVENT") val event: String,
    @SerialName("CONDITIONS") val conditions: List<String>,
    @SerialName("OPERATIONS") val operations: List<String>,
)
