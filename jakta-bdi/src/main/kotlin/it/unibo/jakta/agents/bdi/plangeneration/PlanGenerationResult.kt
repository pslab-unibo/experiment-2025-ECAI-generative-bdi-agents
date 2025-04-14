package it.unibo.jakta.agents.bdi.plangeneration

import it.unibo.jakta.agents.bdi.plans.PlanLibrary

interface PlanGenerationResult : GenerationResult {
    val generationState: GenerationState
    val generatedPlanLibrary: PlanLibrary

    fun copy(
        generationState: GenerationState = this.generationState,
        generatedPlanLibrary: PlanLibrary = this.generatedPlanLibrary,
    ): PlanGenerationResult
}
