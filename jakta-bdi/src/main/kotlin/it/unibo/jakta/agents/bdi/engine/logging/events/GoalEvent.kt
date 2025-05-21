package it.unibo.jakta.agents.bdi.engine.logging.events

import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("GoalEvent")
sealed interface GoalEvent : AgentEvent {
    @Serializable
    @SerialName("GoalAchieved")
    data class GoalAchieved(
        val goal: Goal,
        val planID: PlanID,
        override val description: String,
    ) : GoalEvent {
        constructor(goal: Goal, planID: PlanID) : this(goal, planID, "Achieved goal $goal")
    }
}
