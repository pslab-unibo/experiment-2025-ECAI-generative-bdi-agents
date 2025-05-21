package it.unibo.jakta.agents.bdi.generationstrategies.lm

import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.PromptBuilder
import kotlinx.serialization.Serializable
import kotlin.time.Duration

interface LMGenerationConfig : GenerationConfig {
    val modelId: String?
    val temperature: Double?
    val maxTokens: Int?
    val lmServerUrl: String?
    val lmServerToken: String?
    val contextFilter: ContextFilter?
    val promptBuilder: PromptBuilder?
    val remarks: Iterable<Remark>?
    val requestTimeout: Duration?
    val connectTimeout: Duration?
    val socketTimeout: Duration?

    @Serializable
    data class LMGenerationConfigContainer(
        override val modelId: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
        override val temperature: Double = DefaultGenerationConfig.DEFAULT_TEMPERATURE,
        override val maxTokens: Int = DefaultGenerationConfig.DEFAULT_MAX_TOKENS,
        override val lmServerUrl: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
        override val lmServerToken: String = DefaultGenerationConfig.DEFAULT_LM_SERVER_URL,
        override val contextFilter: ContextFilter = DefaultFilters.defaultFilter,
        override val promptBuilder: PromptBuilder = DefaultPromptBuilder.descriptivePrompt,
        override val remarks: Iterable<Remark> = emptyList(),
        override val requestTimeout: Duration = DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT,
        override val connectTimeout: Duration = DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT,
        override val socketTimeout: Duration = DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT,
    ) : LMGenerationConfig

    data class LMGenerationConfigUpdate(
        override val modelId: String? = null,
        override val temperature: Double? = null,
        override val maxTokens: Int? = null,
        override val lmServerUrl: String? = null,
        override val lmServerToken: String? = null,
        override val contextFilter: ContextFilter? = null,
        override val promptBuilder: PromptBuilder? = null,
        override val remarks: Iterable<Remark>? = null,
        override val requestTimeout: Duration? = null,
        override val connectTimeout: Duration? = null,
        override val socketTimeout: Duration? = null,
    ) : LMGenerationConfig {
        fun fromConfig(
            config: LMGenerationConfigContainer = LMGenerationConfigContainer(),
        ): LMGenerationConfigContainer =
            LMGenerationConfigContainer(
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
