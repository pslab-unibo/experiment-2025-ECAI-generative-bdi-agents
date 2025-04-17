package it.unibo.jakta.generationstrategies.lm.configuration

import it.unibo.jakta.agents.bdi.plangeneration.GenerationConfig

data class LMGenerationConfig(
    val modelId: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
    val temperature: Double = DefaultGenerationConfig.DEFAULT_TEMPERATURE,
    val maxTokens: Int = DefaultGenerationConfig.DEFAULT_MAX_TOKENS,
) : GenerationConfig
