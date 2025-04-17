package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

class LMPlanGenerationResult(
    override val generationState: GenerationState,
    override val generatedPlanLibrary: PlanLibrary,
) : PlanGenerationResult {
    constructor(
        generationState: GenerationState,
        generatedPlan: Plan,
    ) : this(generationState, PlanLibrary.of(generatedPlan))

    constructor(
        generationState: GenerationState,
    ) : this(generationState, PlanLibrary.empty())

    override fun copy(
        generationState: GenerationState,
        generatedPlanLibrary: PlanLibrary,
    ): LMPlanGenerationResult =
        LMPlanGenerationResult(generationState, generatedPlanLibrary)

    operator fun plus(other: LMPlanGenerationResult) =
        copy(generatedPlanLibrary = generatedPlanLibrary + other.generatedPlanLibrary)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LMPlanGenerationResult

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
