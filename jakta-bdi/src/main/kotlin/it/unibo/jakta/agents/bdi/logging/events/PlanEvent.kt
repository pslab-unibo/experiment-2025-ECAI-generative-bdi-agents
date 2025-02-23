package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface PlanEvent : LogEvent

data class PlanSelected(
    val plan: Plan,
) : PlanEvent {
    override val description = "Plan ${plan.trigger.value} selected"

    override val params = super.params + buildMap(capacity = 2) {
        put("trigger", plan.trigger)
        put("guard", plan.guard)
        put("goals", plan.goals)
    }
}
