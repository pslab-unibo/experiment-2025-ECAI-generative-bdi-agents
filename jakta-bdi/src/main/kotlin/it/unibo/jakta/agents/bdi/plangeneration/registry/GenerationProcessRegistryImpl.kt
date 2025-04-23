package it.unibo.jakta.agents.bdi.plangeneration.registry

import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState

class GenerationProcessRegistryImpl(
    val from: Map<GeneratePlan, GenerationState> = emptyMap(),
) : GenerationProcessRegistry, LinkedHashMap<GeneratePlan, GenerationState>(from) {

    override fun updateGenerationProcess(
        goal: GeneratePlan,
        generationState: GenerationState,
    ): GenerationProcessRegistry =
        GenerationProcessRegistryImpl(this + Pair(goal, generationState))

    override fun nextGenerationState(): GenerationState? {
        if (this.isEmpty()) return null
        return this.entries.iterator().next().value
    }

    override fun pop(): GenerationProcessRegistry =
        nextGenerationState()?.let { GenerationProcessRegistryImpl(this - it.goal) } ?: this

    override fun deleteGenerationProcess(goal: GeneratePlan): GenerationProcessRegistry =
        GenerationProcessRegistryImpl(this - goal)

    override fun toString(): String = from.values.joinToString(separator = "\n")
}
