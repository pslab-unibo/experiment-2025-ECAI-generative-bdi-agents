package it.unibo.jakta.generationstrategies.lm.dsl

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.generationstrategies.lm.DefaultGenerationConfig
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.Remark
import kotlin.collections.plusAssign
import kotlin.time.Duration

class LMGenerationConfigScope : Builder<LMGenerationConfig> {
    var model: String = DefaultGenerationConfig.DEFAULT_MODEL_ID
    var temperature: Double = DefaultGenerationConfig.DEFAULT_TEMPERATURE
    var maxTokens: Int = DefaultGenerationConfig.DEFAULT_MAX_TOKENS
    var url: String = DefaultGenerationConfig.DEFAULT_LM_SERVER_URL
    var token: String = DefaultGenerationConfig.DEFAULT_TOKEN
    var requestTimeout: Duration = DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT
    var connectTimeout: Duration = DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT
    var socketTimeout: Duration = DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT
    var withSubgoals: Boolean = false

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

    override fun build(): LMGenerationConfig {
        return LMGenerationConfig(
            model,
            temperature,
            maxTokens,
            url,
            token,
            remarks,
            withSubgoals,
            requestTimeout,
            connectTimeout,
            socketTimeout,
        )
    }
}
