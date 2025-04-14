package it.unibo.jakta.agents.bdi.plangeneration

import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

object Common {
    fun getStrategyFromID(planID: PlanID, planLibrary: PlanLibrary): GenerationStrategy? {
        val genPlan = planLibrary.plans.firstOrNull { it.id == planID }
        return if (genPlan != null && genPlan is PartialPlan) {
            genPlan.generationStrategy
        } else {
            null
        }
    }
}
