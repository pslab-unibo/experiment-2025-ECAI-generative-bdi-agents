package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_LM_SERVER_URL
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_MAX_TOKENS
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_MODEL_ID
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_TEMPERATURE
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_TOKEN

@JvmInline
value class Remark(val value: String)

data class LMGenScopeConfig(
    val lmInitCfg: LMInitialConfig,
    val lmGenCfg: LMGenerationConfig,
)

data class LMInitialConfig(
    val lmServerUrl: String,
    val lmServerToken: String,
    val remarks: List<Remark> = emptyList(),
)

class LMGenScope : Builder<LMGenScopeConfig> {
    var model: String = DEFAULT_MODEL_ID
    var temperature: Double = DEFAULT_TEMPERATURE
    var maxTokens: Int = DEFAULT_MAX_TOKENS
    var url: String = DEFAULT_LM_SERVER_URL
    var token: String = DEFAULT_TOKEN

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

    override fun build(): LMGenScopeConfig {
        return LMGenScopeConfig(
            LMInitialConfig(url, token, remarks),
            LMGenerationConfig(model, temperature, maxTokens),
        )
    }
}
