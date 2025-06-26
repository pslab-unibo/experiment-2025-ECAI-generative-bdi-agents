package it.unibo.jakta.agents.bdi.engine.generation.registry.impl

import it.unibo.jakta.agents.bdi.engine.generation.GenerationState
import it.unibo.jakta.agents.bdi.engine.generation.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan

internal class GenerationProcessRegistryImpl(
    val from: Map<GeneratePlan, GenerationState> = emptyMap(),
) : LinkedHashMap<GeneratePlan, GenerationState>(from),
    GenerationProcessRegistry {
    override fun updateGenerationProcess(generationState: GenerationState): GenerationProcessRegistry =
        GenerationProcessRegistryImpl(this + Pair(generationState.goal, generationState))

    override fun nextGenerationState(): GenerationState? {
        if (this.isEmpty()) return null
        return this.entries
            .iterator()
            .next()
            .value
    }

    override fun pop(): GenerationProcessRegistry =
        nextGenerationState()?.let { GenerationProcessRegistryImpl(this - it.goal) } ?: this

    override fun deleteGenerationProcess(goal: GeneratePlan): GenerationProcessRegistry =
        GenerationProcessRegistryImpl(this - goal)

    override fun toString(): String = from.values.joinToString(separator = "\n")
}
