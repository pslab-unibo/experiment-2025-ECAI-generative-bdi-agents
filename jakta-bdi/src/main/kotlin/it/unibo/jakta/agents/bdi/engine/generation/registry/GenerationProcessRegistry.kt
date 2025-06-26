package it.unibo.jakta.agents.bdi.engine.generation.registry

import it.unibo.jakta.agents.bdi.engine.generation.GenerationState
import it.unibo.jakta.agents.bdi.engine.generation.registry.impl.GenerationProcessRegistryImpl
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan

interface GenerationProcessRegistry : Map<GeneratePlan, GenerationState> {
    fun updateGenerationProcess(generationState: GenerationState): GenerationProcessRegistry

    fun nextGenerationState(): GenerationState?

    fun pop(): GenerationProcessRegistry

    fun deleteGenerationProcess(goal: GeneratePlan): GenerationProcessRegistry

    companion object {
        fun empty(): GenerationProcessRegistry = GenerationProcessRegistryImpl()

        fun of(requests: Map<GeneratePlan, GenerationState>): GenerationProcessRegistry =
            GenerationProcessRegistryImpl(
                requests,
            )
    }
}
