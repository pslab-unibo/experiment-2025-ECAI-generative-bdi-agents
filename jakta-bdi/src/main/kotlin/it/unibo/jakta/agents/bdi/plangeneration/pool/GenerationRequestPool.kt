package it.unibo.jakta.agents.bdi.plangeneration.pool

import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plans.PlanID

interface GenerationRequestPool : Map<PlanID, GenerationState> {

    fun updateRequest(planID: PlanID, generationState: GenerationState): GenerationRequestPool

    fun deleteRequest(planID: PlanID): GenerationRequestPool

    companion object {
        fun empty(): GenerationRequestPool = GenerationRequestPoolImpl()

        fun of(requests: Map<PlanID, GenerationState>): GenerationRequestPool = GenerationRequestPoolImpl(requests)
    }
}
