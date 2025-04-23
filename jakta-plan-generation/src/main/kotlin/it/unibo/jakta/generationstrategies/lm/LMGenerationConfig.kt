package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.plangeneration.GenerationConfig
import kotlin.time.Duration

data class LMGenerationConfig(
    val modelId: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
    val temperature: Double = DefaultGenerationConfig.DEFAULT_TEMPERATURE,
    val maxTokens: Int = DefaultGenerationConfig.DEFAULT_MAX_TOKENS,
    val lmServerUrl: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
    val lmServerToken: String = DefaultGenerationConfig.DEFAULT_LM_SERVER_URL,
    val remarks: List<Remark> = emptyList(),
    val withSubgoals: Boolean = false,
    val requestTimeout: Duration = DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT,
    val connectTimeout: Duration = DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT,
    val socketTimeout: Duration = DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT,
) : GenerationConfig
