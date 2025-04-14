package it.unibo.jakta.agents.bdi.plangeneration.registry

import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plans.PlanID

class GenerationProcessRegistryImpl(
    val from: Map<PlanID, GenerationState> = emptyMap(),
) : GenerationProcessRegistry, HashMap<PlanID, GenerationState>(from) {

    override fun updateGenerationProcess(
        planID: PlanID,
        generationState: GenerationState,
    ): GenerationProcessRegistry =
        GenerationProcessRegistryImpl(this + Pair(planID, generationState))

    override fun deleteGenerationProcess(planID: PlanID): GenerationProcessRegistry =
        GenerationProcessRegistryImpl(this - planID)

    override fun toString(): String = from.values.joinToString()
}
