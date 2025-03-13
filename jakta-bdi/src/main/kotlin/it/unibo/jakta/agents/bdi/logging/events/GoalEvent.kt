package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct

sealed interface GoalEvent : LogEvent

data class GoalCreated(
    val goal: Goal,
) : GoalEvent {
    val goalValue = formatter.format(goal.value)

    override val description = "Goal $goalValue created, state: pending"
}

//    data class GoalSuspended(
//        val goal: Goal,
//    ): GoalEvent {
//        override val description = "Goal ${goal.value} suspended"
//    }

data class GoalAchieved(
    val goal: Struct,
    val planID: PlanID,
) : GoalEvent {
    val goalValue = formatter.format(goal)

    override val description = "Achieved goal $goalValue"

    override val params: Map<String, Any?> = super.params + buildMap {
        put("goal", goal)
        put("planID", planID.id)
        put("planTrigger", planID.trigger)
    }
}

data class GoalFailed(
    val goal: Struct,
) : GoalEvent {
    val goalValue = formatter.format(goal)

    override val description = "Failed goal $goalValue"
}
