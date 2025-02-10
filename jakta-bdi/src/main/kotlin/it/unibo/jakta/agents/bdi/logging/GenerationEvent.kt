package it.unibo.jakta.agents.bdi.logging

import it.unibo.jakta.agents.bdi.plans.Plan

data class GenerationEvent(
    val generatedPlan: Plan,
) : LogEvent {
    override val description: String = "Generated new plan $generatedPlan"
}
