package it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.impl

import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.LMGenerationConfigScope
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy

object GenerationStrategies {
    fun oneStep(generationCfg: LMGenerationConfig.LMGenerationConfigContainer) = LMGenerationStrategy.of(generationCfg)

    fun oneStep(generationCfg: LMGenerationConfigScope.() -> Unit): LMGenerationStrategy {
        val res = LMGenerationConfigScope().also(generationCfg).build().fromConfig()
        return LMGenerationStrategy.of(res)
    }
}
