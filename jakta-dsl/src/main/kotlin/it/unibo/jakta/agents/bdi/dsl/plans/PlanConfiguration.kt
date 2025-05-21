package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy

data class PlanConfiguration(
    val generationStrategy: GenerationStrategy?,
)
