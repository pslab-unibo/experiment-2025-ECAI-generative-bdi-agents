package it.unibo.jakta.playground.evaluation.gendata

import kotlinx.serialization.Serializable

@Serializable
data class GenerationResponse(
    val data: GenerationData,
)
