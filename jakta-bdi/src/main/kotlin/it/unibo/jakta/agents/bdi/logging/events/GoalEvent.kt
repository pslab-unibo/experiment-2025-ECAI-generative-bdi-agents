package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.PlanID

sealed interface GoalEvent : LogEvent {
    data class GoalAchieved(
        val goal: Goal,
        val planID: PlanID,
    ) : GoalEvent {
        override val description = "Achieved goal $goal"

        override val metadata: Map<String, Any?> = super.metadata + buildMap {
            put("goal", goal)
            put("trigger", planID.trigger)
            put("guard", planID.context)
        }
    }
}
