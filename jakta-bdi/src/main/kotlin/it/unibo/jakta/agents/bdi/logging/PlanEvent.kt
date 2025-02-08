package it.unibo.jakta.agents.bdi.logging

import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface PlanEvent : LogEvent

data class PlanSelected(
    val plan: Plan,
) : PlanEvent {
    override val description =
        "Plan ${plan.trigger.value} selected"
}
