package it.unibo.jakta.generationstrategies.lm.configuration

import it.unibo.jakta.generationstrategies.lm.Remark
import kotlin.time.Duration

data class LMInitialConfig(
    val lmServerUrl: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
    val lmServerToken: String = DefaultGenerationConfig.DEFAULT_LM_SERVER_URL,
    val promptPath: String? = null,
    val remarks: List<Remark> = emptyList(),
    val requestTimeout: Duration = DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT,
    val connectTimeout: Duration = DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT,
    val socketTimeout: Duration = DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT,
)
