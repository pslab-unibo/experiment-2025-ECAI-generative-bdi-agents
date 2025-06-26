package it.unibo.jakta.agents.bdi.generationstrategies.lm

import kotlin.time.Duration.Companion.seconds

object DefaultGenerationConfig {
    const val DEFAULT_MODEL_ID = ""
    const val DEFAULT_TEMPERATURE = 0.5
    const val DEFAULT_MAX_TOKENS = 2048
    const val DEFAULT_LM_SERVER_URL = "http://localhost:8080"
    const val DEFAULT_TOKEN = ""
    val DEFAULT_REQUEST_TIMEOUT = 120.seconds
    val DEFAULT_CONNECT_TIMEOUT = 10.seconds
    val DEFAULT_SOCKET_TIMEOUT = 60.seconds
}
