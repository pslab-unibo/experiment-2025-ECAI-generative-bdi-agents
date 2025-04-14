package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.pool.GenerationRequestPool
import it.unibo.jakta.agents.bdi.plans.PlanID

class GenerationRequestManager(private val logger: KLogger?) {

    fun updateGenerationRequests(
        context: AgentContext,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): GenerationRequestPool {
        return if (!planGenResult.generationState.isGenerationEndConfirmed) {
            // Continue tracking this request and update its state.
            context.generationRequests.updateRequest(planID, planGenResult.generationState)
        } else {
            // Since the generation ended, remove the request.
            logger?.info { "Deleting generation request" }
            context.generationRequests.deleteRequest(planID)
        }
    }
}
