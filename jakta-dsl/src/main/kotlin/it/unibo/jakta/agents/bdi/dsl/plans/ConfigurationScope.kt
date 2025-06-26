package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy

class ConfigurationScope : ScopeBuilder<PlanConfiguration> {
    var generationStrategy: GenerationStrategy? = null

    override fun build(): PlanConfiguration = PlanConfiguration(generationStrategy)
}
