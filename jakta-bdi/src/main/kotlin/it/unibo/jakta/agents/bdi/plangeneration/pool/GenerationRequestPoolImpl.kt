package it.unibo.jakta.agents.bdi.plangeneration.pool

import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plans.PlanID

class GenerationRequestPoolImpl(
    val from: Map<PlanID, GenerationState> = emptyMap(),
) : GenerationRequestPool, HashMap<PlanID, GenerationState>(from) {

    override fun updateRequest(
        planID: PlanID,
        generationState: GenerationState,
    ): GenerationRequestPool =
        GenerationRequestPoolImpl(this + Pair(planID, generationState))

    override fun deleteRequest(planID: PlanID): GenerationRequestPool =
        GenerationRequestPoolImpl(this - planID)

    override fun toString(): String = from.values.joinToString(separator = "\n")
}
