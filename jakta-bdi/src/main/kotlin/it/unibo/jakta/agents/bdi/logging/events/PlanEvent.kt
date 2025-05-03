package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter
import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface PlanEvent : LogEvent {
    data class PlanSelected(
        val plan: Plan,
    ) : PlanEvent {
        val trigger = triggerFormatter.format(plan.trigger)

        override val description = "Selected plan $trigger"

        override val metadata = super.metadata + buildMap {
            put("plan", plan)
        }
    }
}
