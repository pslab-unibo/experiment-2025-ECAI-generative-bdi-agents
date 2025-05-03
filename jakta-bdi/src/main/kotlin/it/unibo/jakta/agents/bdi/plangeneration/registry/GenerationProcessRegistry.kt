package it.unibo.jakta.agents.bdi.plangeneration.registry

import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState

interface GenerationProcessRegistry : Map<GeneratePlan, GenerationState> {

    fun updateGenerationProcess(generationState: GenerationState): GenerationProcessRegistry

    fun nextGenerationState(): GenerationState?

    fun pop(): GenerationProcessRegistry

    fun deleteGenerationProcess(goal: GeneratePlan): GenerationProcessRegistry

    companion object {
        fun empty(): GenerationProcessRegistry = GenerationProcessRegistryImpl()

        fun of(requests: Map<GeneratePlan, GenerationState>): GenerationProcessRegistry = GenerationProcessRegistryImpl(
            requests,
        )
    }
}
