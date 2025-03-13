package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.plans.GeneratedPlan

data class GenerationEvent(
    val generatedPlan: GeneratedPlan,
) : LogEvent {
    override val description: String = "Generated new plan with goals ${generatedPlan.goals}"

    override val params = super.params + buildMap {
        put("trigger", generatedPlan.trigger)
        put("guard", generatedPlan.guard)
        put("goals", generatedPlan.goals)
        put("generationStrategy", generatedPlan.generationStrategy)
    }
}
