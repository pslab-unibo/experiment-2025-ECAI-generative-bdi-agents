package it.unibo.jakta.agents.bdi.generationstrategies.lm

import it.unibo.jakta.agents.bdi.engine.generation.GenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_CONNECT_TIMEOUT
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_LM_SERVER_URL
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_MAX_TOKENS
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_MODEL_ID
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_REQUEST_TIMEOUT
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_SOCKET_TIMEOUT
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_TEMPERATURE
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_TOKEN
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters.metaPlanFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder.systemPrompt
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder.userPromptWithHintsAndRemarks
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.SystemPromptBuilder
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.UserPromptBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.time.Duration

sealed interface LMGenerationConfig : GenerationConfig {
    val modelId: String?
    val temperature: Double?
    val maxTokens: Int?
    val lmServerUrl: String?
    val lmServerToken: String?
    val contextFilters: Iterable<ContextFilter>
    val contextFiltersNames: List<String>
    val systemPromptBuilder: SystemPromptBuilder?
    val systemPromptBuilderName: String?
    val userPromptBuilder: UserPromptBuilder?
    val userPromptBuilderName: String?
    val remarks: Iterable<Remark>?
    val requestTimeout: Duration?
    val connectTimeout: Duration?
    val socketTimeout: Duration?

    @Serializable
    @SerialName("LMGenerationConfigContainer")
    data class LMGenerationConfigContainer(
        override val modelId: String = DEFAULT_MODEL_ID,
        override val temperature: Double = DEFAULT_TEMPERATURE,
        override val maxTokens: Int = DEFAULT_MAX_TOKENS,
        override val lmServerUrl: String = DEFAULT_LM_SERVER_URL,
        @Transient
        override val lmServerToken: String = DEFAULT_TOKEN,
        @Transient
        override val contextFilters: List<ContextFilter> = listOf(metaPlanFilter),
        @Transient
        override val systemPromptBuilder: SystemPromptBuilder? = systemPrompt,
        @Transient
        override val userPromptBuilder: UserPromptBuilder = userPromptWithHintsAndRemarks,
        override val contextFiltersNames: List<String> = contextFilters.map { it.name },
        override val systemPromptBuilderName: String? = systemPromptBuilder?.name,
        override val userPromptBuilderName: String? = userPromptBuilder.name,
        override val remarks: List<Remark> = emptyList(),
        override val requestTimeout: Duration = DEFAULT_REQUEST_TIMEOUT,
        override val connectTimeout: Duration = DEFAULT_CONNECT_TIMEOUT,
        override val socketTimeout: Duration = DEFAULT_SOCKET_TIMEOUT,
    ) : LMGenerationConfig

    @Serializable
    @SerialName("LMGenerationConfigUpdate")
    data class LMGenerationConfigUpdate(
        override val modelId: String? = null,
        override val temperature: Double? = null,
        override val maxTokens: Int? = null,
        override val lmServerUrl: String? = null,
        @Transient
        override val lmServerToken: String? = null,
        @Transient
        override val contextFilters: List<ContextFilter> = emptyList(),
        @Transient
        override val systemPromptBuilder: SystemPromptBuilder? = null,
        @Transient
        override val userPromptBuilder: UserPromptBuilder? = null,
        override val contextFiltersNames: List<String> = emptyList(),
        override val systemPromptBuilderName: String? = null,
        override val userPromptBuilderName: String? = null,
        override val remarks: List<Remark>? = null,
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
                systemPromptBuilder = this.systemPromptBuilder ?: config.systemPromptBuilder,
                userPromptBuilder = this.userPromptBuilder ?: config.userPromptBuilder,
                remarks = this.remarks ?: config.remarks,
                requestTimeout = this.requestTimeout ?: config.requestTimeout,
                connectTimeout = this.connectTimeout ?: config.connectTimeout,
            )
    }
}
