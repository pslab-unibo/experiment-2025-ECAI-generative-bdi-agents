package it.unibo.jakta.agents.bdi.generationstrategies.lm

import it.unibo.jakta.agents.bdi.engine.generation.GenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.PromptBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration

interface LMGenerationConfig : GenerationConfig {
    val modelId: String?
    val temperature: Double?
    val maxTokens: Int?
    val lmServerUrl: String?
    val lmServerToken: String?
    val contextFilters: Iterable<ContextFilter>
    val promptBuilder: PromptBuilder?
    val remarks: Iterable<Remark>?
    val requestTimeout: Duration?
    val connectTimeout: Duration?
    val socketTimeout: Duration?

    @Serializable
    @SerialName("LMGenerationConfig")
    data class LMGenerationConfigContainer(
        override val modelId: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
        override val temperature: Double = DefaultGenerationConfig.DEFAULT_TEMPERATURE,
        override val maxTokens: Int = DefaultGenerationConfig.DEFAULT_MAX_TOKENS,
        override val lmServerUrl: String = DefaultGenerationConfig.DEFAULT_MODEL_ID,
        override val lmServerToken: String = DefaultGenerationConfig.DEFAULT_LM_SERVER_URL,
        @Transient
        override val contextFilters: List<ContextFilter> = listOf(DefaultFilters.metaPlanFilter),
        @Transient
        override val promptBuilder: PromptBuilder = DefaultPromptBuilder.promptWithHints,
        override val remarks: Iterable<Remark> = emptyList(),
        override val requestTimeout: Duration = DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT,
        override val connectTimeout: Duration = DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT,
        override val socketTimeout: Duration = DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT,
    ) : LMGenerationConfig

    @Serializable
    @SerialName("LMGenerationConfigUpdate")
    data class LMGenerationConfigUpdate(
        override val modelId: String? = null,
        override val temperature: Double? = null,
        override val maxTokens: Int? = null,
        override val lmServerUrl: String? = null,
        override val lmServerToken: String? = null,
        @Transient
        override val contextFilters: List<ContextFilter> = emptyList(),
        @Transient
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
                contextFilters = this.contextFilters.ifEmpty { config.contextFilters },
                promptBuilder = this.promptBuilder ?: config.promptBuilder,
                remarks = this.remarks ?: config.remarks,
                requestTimeout = this.requestTimeout ?: config.requestTimeout,
                connectTimeout = this.connectTimeout ?: config.connectTimeout,
            )
    }
}
