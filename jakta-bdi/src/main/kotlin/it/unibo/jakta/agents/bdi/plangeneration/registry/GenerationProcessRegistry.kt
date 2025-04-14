package it.unibo.jakta.agents.bdi.plangeneration.registry

import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plans.PlanID

interface GenerationProcessRegistry : Map<PlanID, GenerationState> {

    fun updateGenerationProcess(planID: PlanID, generationState: GenerationState): GenerationProcessRegistry

    fun deleteGenerationProcess(planID: PlanID): GenerationProcessRegistry

    companion object {
        fun empty(): GenerationProcessRegistry = GenerationProcessRegistryImpl()

        fun of(requests: Map<PlanID, GenerationState>): GenerationProcessRegistry = GenerationProcessRegistryImpl(
            requests,
        )
    }
}
