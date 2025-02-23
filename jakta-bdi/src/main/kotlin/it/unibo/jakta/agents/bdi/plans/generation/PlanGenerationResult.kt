package it.unibo.jakta.agents.bdi.plans.generation

import it.unibo.jakta.agents.bdi.plans.Plan

data class PlanGenerationResult(
    val generatedPlan: Plan? = null,
    val errorMsg: String? = null,
    val trials: Int = 0,
    val maxTrials: Int = 3,
)
