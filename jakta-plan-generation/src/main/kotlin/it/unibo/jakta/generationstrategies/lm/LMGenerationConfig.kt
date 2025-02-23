package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.plans.generation.GenerationConfig

data class LMGenerationConfig(
    val modelId: String,
    val temperature: Double,
    val maxTokens: Int,
) : GenerationConfig
