package it.unibo.jakta.agents.bdi.generationstrategies

import it.unibo.jakta.agents.bdi.plans.Plan

data class PlanGenerationResult(
    val generatedPlan: Plan? = null,
    val errorMsg: String? = null,
)
