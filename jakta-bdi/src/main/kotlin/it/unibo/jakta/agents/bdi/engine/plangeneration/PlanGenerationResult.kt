package it.unibo.jakta.agents.bdi.engine.plangeneration

import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.plans.PartialPlan

interface PlanGenerationResult : GenerationResult {
    val generatedPlanLibrary: List<PartialPlan>
    val generatedAdmissibleGoals: Set<AdmissibleGoal>
    val generatedAdmissibleBeliefs: Set<AdmissibleBelief>

    fun copy(
        generationState: GenerationState = this.generationState,
        generatedPlanLibrary: List<PartialPlan> = this.generatedPlanLibrary,
        generatedAdmissibleGoals: Set<AdmissibleGoal> = this.generatedAdmissibleGoals,
        generatedAdmissibleBeliefs: Set<AdmissibleBelief> = this.generatedAdmissibleBeliefs,
    ): PlanGenerationResult
}
