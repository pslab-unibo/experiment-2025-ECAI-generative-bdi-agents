package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy

data class PlanGenerationConfig(
    val generate: Boolean,
    val generationStrategy: GenerationStrategy?,
)
