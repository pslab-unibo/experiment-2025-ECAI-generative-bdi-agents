package it.unibo.jakta.generationstrategies.lm.strategy

import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.dsl.LMGenerationConfigScope

object GenerationStrategies {
    fun oneStep(generationCfg: LMGenerationConfig.LMGenerationConfigContainer) =
        LMGenerationStrategy.of(generationCfg)

    fun oneStep(generationCfg: LMGenerationConfigScope.() -> Unit): LMGenerationStrategy {
        val res = LMGenerationConfigScope().also(generationCfg).build().fromConfig()
        return LMGenerationStrategy.of(res)
    }
}
