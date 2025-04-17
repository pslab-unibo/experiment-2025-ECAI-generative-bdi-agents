package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy

class ConfigurationScope : Builder<PlanConfiguration> {
    var generationStrategy: GenerationStrategy? = null

    override fun build(): PlanConfiguration =
        PlanConfiguration(generationStrategy)
}
