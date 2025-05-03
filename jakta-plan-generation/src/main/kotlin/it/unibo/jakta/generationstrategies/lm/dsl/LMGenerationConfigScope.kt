package it.unibo.jakta.generationstrategies.lm.dsl

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig.LMGenerationConfigUpdate
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import kotlin.time.Duration

class LMGenerationConfigScope : Builder<LMGenerationConfigUpdate> {
    var model: String? = null
    var temperature: Double? = null
    var maxTokens: Int? = null
    var url: String? = null
    var token: String? = null
    var requestTimeout: Duration? = null
    var connectTimeout: Duration? = null
    var socketTimeout: Duration? = null
    var contextFilter: ContextFilter? = null
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
            contextFilter,
            promptBuilder,
            remarks,
            requestTimeout,
            connectTimeout,
            socketTimeout,
        )
}
