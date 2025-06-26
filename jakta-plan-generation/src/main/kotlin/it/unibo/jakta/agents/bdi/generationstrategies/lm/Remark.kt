package it.unibo.jakta.agents.bdi.generationstrategies.lm

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JvmInline
@Serializable
@SerialName("Remark")
value class Remark(
    val value: String,
)
