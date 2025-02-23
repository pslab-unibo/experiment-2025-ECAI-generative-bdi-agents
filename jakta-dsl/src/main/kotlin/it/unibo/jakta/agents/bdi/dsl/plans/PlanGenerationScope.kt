package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy

class PlanGenerationScope : Builder<PlanGenerationConfig> {
    var generate = false
    var generationStrategy: GenerationStrategy? = null

    override fun build(): PlanGenerationConfig =
        PlanGenerationConfig(generate, generationStrategy)
}
