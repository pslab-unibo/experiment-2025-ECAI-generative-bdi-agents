package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.generationstrategies.lm.configuration.DefaultGenerationConfig
import it.unibo.jakta.generationstrategies.lm.configuration.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.configuration.LMInitialConfig
import it.unibo.jakta.generationstrategies.lm.configuration.LanguageModelConfig
import kotlin.time.Duration

class LMGenScope : Builder<LanguageModelConfig> {
    var model: String = DefaultGenerationConfig.DEFAULT_MODEL_ID
    var temperature: Double = DefaultGenerationConfig.DEFAULT_TEMPERATURE
    var maxTokens: Int = DefaultGenerationConfig.DEFAULT_MAX_TOKENS
    var url: String = DefaultGenerationConfig.DEFAULT_LM_SERVER_URL
    var token: String = DefaultGenerationConfig.DEFAULT_TOKEN
    var requestTimeout: Duration = DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT
    var connectTimeout: Duration = DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT
    var socketTimeout: Duration = DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT

    val remarks = mutableListOf<Remark>()

    /**
     * Handler for the addition of a remark to the prompt of an LLM.
     * @param remark the [String] that provides additional context in the prompt.
     */
    fun remark(remark: String) {
        remarks += Remark(remark)
    }

    fun remarks(vararg remark: String) {
        remarks.addAll(remark.map { Remark(it) })
    }

    override fun build(): LanguageModelConfig {
        return LanguageModelConfig(
            LMInitialConfig(
                url,
                token,
                remarks = remarks,
                requestTimeout = requestTimeout,
                connectTimeout = connectTimeout,
                socketTimeout = socketTimeout,
            ),
            LMGenerationConfig(model, temperature, maxTokens),
        )
    }
}
