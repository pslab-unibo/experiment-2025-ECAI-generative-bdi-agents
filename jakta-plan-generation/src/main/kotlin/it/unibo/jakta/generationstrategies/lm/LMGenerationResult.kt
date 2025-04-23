package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plans.PartialPlan

class LMGenerationResult(
    override val generationState: GenerationState,
    override val generatedPlanLibrary: List<PartialPlan>,
    override val generatedAdmissibleGoals: Set<AdmissibleGoal>,
    override val generatedAdmissibleBeliefs: Set<AdmissibleBelief>,
) : PlanGenerationResult {

    override fun copy(
        generationState: GenerationState,
        generatedPlanLibrary: List<PartialPlan>,
        generatedAdmissibleGoals: Set<AdmissibleGoal>,
        generatedAdmissibleBeliefs: Set<AdmissibleBelief>,
    ): LMGenerationResult =
        LMGenerationResult(
            generationState,
            generatedPlanLibrary,
            generatedAdmissibleGoals,
            generatedAdmissibleBeliefs,
        )

    operator fun plus(other: LMGenerationResult) =
        copy(generatedPlanLibrary = generatedPlanLibrary + other.generatedPlanLibrary)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LMGenerationResult

        if (generationState != other.generationState) return false
        if (generatedPlanLibrary != other.generatedPlanLibrary) return false

        return true
    }

    override fun hashCode(): Int {
        var result = generationState.hashCode()
        result = 31 * result + generatedPlanLibrary.hashCode()
        return result
    }

    override fun toString(): String {
        return "LMPlanGenerationResult(generationState=$generationState, generatedPlanLibrary=$generatedPlanLibrary)"
    }
}
