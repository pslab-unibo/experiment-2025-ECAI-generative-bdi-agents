package it.unibo.jakta.agents.bdi.generationstrategies.lm

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Remark(
    val value: String,
)
