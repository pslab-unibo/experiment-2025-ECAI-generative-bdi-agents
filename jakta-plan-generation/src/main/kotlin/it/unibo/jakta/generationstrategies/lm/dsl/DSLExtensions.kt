package it.unibo.jakta.generationstrategies.lm.dsl

import it.unibo.jakta.agents.bdi.dsl.AgentScope
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.plans.PlanScope
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.strategy.GenerationStrategies

object DSLExtensions {
    fun MasScope.oneStepGeneration(config: LMGenerationConfigScope.() -> Unit): MasScope {
        generationStrategy = GenerationStrategies.oneStep(config)
        return this
    }

    fun AgentScope.oneStepGeneration(config: LMGenerationConfigScope.() -> Unit): AgentScope {
        generationStrategy = GenerationStrategies.oneStep(config)
        return this
    }

    fun oneStepGeneration(config: LMGenerationConfigScope.() -> Unit): GenerationStrategy? =
        GenerationStrategies.oneStep(config)

    infix fun PlanScope.givenLMConfig(given: LMGenerationConfigScope.() -> Unit): PlanScope {
        generationConfig = LMGenerationConfigScope().also(given).build()
        return this
    }
}
