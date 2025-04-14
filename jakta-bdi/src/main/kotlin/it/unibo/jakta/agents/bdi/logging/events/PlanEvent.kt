package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface PlanEvent : LogEvent {
    data class PlanSelected(
        val plan: Plan,
    ) : PlanEvent {
        val trigger = formatter.format(plan.trigger.value)

        override val description = "Selected plan $trigger"

        override val metadata = super.metadata + buildMap {
            put("trigger", plan.trigger)
            put("guard", plan.guard)
            put("goals", plan.goals)
        }
    }
}
