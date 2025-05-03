package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.plangeneration.GenerationConfig
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.DefaultFilters
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import kotlin.time.Duration

interface LMGenerationConfig : GenerationConfig {
    data class LMGenerationConfigContainer(
        val modelId: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
        val temperature: Double = DefaultGenerationConfig.DEFAULT_TEMPERATURE,
        val maxTokens: Int = DefaultGenerationConfig.DEFAULT_MAX_TOKENS,
        val lmServerUrl: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
        val lmServerToken: String = DefaultGenerationConfig.DEFAULT_LM_SERVER_URL,
        val contextFilter: ContextFilter = DefaultFilters.defaultFilter,
        val promptBuilder: PromptBuilder = DefaultPromptBuilder.descriptivePrompt,
        val remarks: Iterable<Remark> = emptyList(),
        val requestTimeout: Duration = DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT,
        val connectTimeout: Duration = DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT,
        val socketTimeout: Duration = DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT,
    ) : LMGenerationConfig

    data class LMGenerationConfigUpdate(
        val modelId: String? = null,
        val temperature: Double? = null,
        val maxTokens: Int? = null,
        val lmServerUrl: String? = null,
        val lmServerToken: String? = null,
        val contextFilter: ContextFilter? = null,
        val promptBuilder: PromptBuilder? = null,
        val remarks: Iterable<Remark>? = null,
        val requestTimeout: Duration? = null,
        val connectTimeout: Duration? = null,
        val socketTimeout: Duration? = null,
    ) : LMGenerationConfig {
        fun fromConfig(
            config: LMGenerationConfigContainer = LMGenerationConfigContainer(),
        ): LMGenerationConfigContainer {
            return LMGenerationConfigContainer(
                modelId = this.modelId ?: config.modelId,
                temperature = this.temperature ?: config.temperature,
                maxTokens = this.maxTokens ?: config.maxTokens,
                lmServerUrl = this.lmServerUrl ?: config.lmServerUrl,
                lmServerToken = this.lmServerToken ?: config.lmServerToken,
                contextFilter = this.contextFilter ?: config.contextFilter,
                promptBuilder = this.promptBuilder ?: config.promptBuilder,
                remarks = this.remarks ?: config.remarks,
                requestTimeout = this.requestTimeout ?: config.requestTimeout,
                connectTimeout = this.connectTimeout ?: config.connectTimeout,
            )
        }
    }
}
