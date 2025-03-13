package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct

sealed interface PlanEvent : LogEvent

data class PlanCompleted(
    val planID: PlanID,
) : PlanEvent {
    val trigger = formatter.format(planID.trigger.value)

    override val description = "Completed plan $trigger"

    override val params = super.params + buildMap {
        put("trigger", planID.trigger)
        put("id", planID.id)
    }
}

data class PlanGenerationStepCompleted(
    val goal: Struct,
    val id: PlanID,
) : PlanEvent {
    val trigger = formatter.format(id.trigger.value)

    override val description = "Generated goal $goal for plan $trigger"
}

data class PlanSelected(
    val plan: Plan,
) : PlanEvent {
    val trigger = formatter.format(plan.trigger.value)

    override val description = "Selected plan $trigger"

    override val params = super.params + buildMap {
        put("trigger", plan.trigger)
        put("guard", plan.guard)
        put("goals", plan.goals)
    }
}
