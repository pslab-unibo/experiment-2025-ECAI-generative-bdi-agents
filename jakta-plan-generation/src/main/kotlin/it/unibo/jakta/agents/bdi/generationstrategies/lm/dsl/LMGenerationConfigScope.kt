package it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl

import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig.LMGenerationConfigUpdate
import it.unibo.jakta.agents.bdi.generationstrategies.lm.Remark
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.PromptBuilder
import kotlin.time.Duration

class LMGenerationConfigScope : ScopeBuilder<LMGenerationConfigUpdate> {
    var model: String? = null
    var temperature: Double? = null
    var maxTokens: Int? = null
    var url: String? = null
    var token: String? = null
    var requestTimeout: Duration? = null
    var connectTimeout: Duration? = null
    var socketTimeout: Duration? = null
    var contextFilters: List<ContextFilter> = emptyList()
    var promptBuilder: PromptBuilder? = null

    private val remarks = mutableListOf<Remark>()

    fun remark(remark: String) {
        remarks += Remark(remark)
    }

    fun remarks(vararg remark: String) {
        remarks.addAll(remark.map { Remark(it) })
    }

    override fun build() =
        LMGenerationConfigUpdate(
            model,
            temperature,
            maxTokens,
            url,
            token,
            contextFilters,
            promptBuilder,
            promptBuilder?.promptName,
            remarks,
            requestTimeout,
            connectTimeout,
            socketTimeout,
        )
}
