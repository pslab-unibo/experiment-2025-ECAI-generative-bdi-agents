package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention.Companion.toIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plans.PlanID

class IntentionManager(private val logger: KLogger?) {
    fun updateIntention(
        intention: DeclarativeIntention,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): Intention {
        return if (planGenResult.generationState.isGenerationEndConfirmed) {
            handleCompletedGeneration(intention, planID)
        } else {
            intention
        }
    }

    private fun handleCompletedGeneration(
        intention: DeclarativeIntention,
        planID: PlanID,
    ): Intention {
        // Remove the completed plan from the generating plans.
        val updatedIntention = intention.copy(generatingPlans = intention.generatingPlans - planID)

        // If no generating plans remain, convert back to normal intention.
        return if (updatedIntention.generatingPlans.isEmpty()) {
            logger?.info { "No other generation processes are running" }
            updatedIntention.toIntention()
        } else {
            updatedIntention
        }.pop()
    }
}
