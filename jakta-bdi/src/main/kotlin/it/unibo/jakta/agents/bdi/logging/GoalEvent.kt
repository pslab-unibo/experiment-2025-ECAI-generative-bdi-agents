package it.unibo.jakta.agents.bdi.logging

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.tuprolog.core.Struct

sealed interface GoalEvent : LogEvent

data class GoalCreated(
    val goal: Goal,
) : GoalEvent {
    override val description = "Goal ${goal.value} created, state: pending"
}

//    data class GoalSuspended(
//        val goal: Goal,
//    ): GoalEvent {
//        override val description = "Goal ${goal.value} suspended"
//    }

data class GoalAchieved(
    val goal: Struct,
) : GoalEvent {
    override val description = "Goal $goal achieved"
}

data class GoalFailed(
    val goal: Struct,
) : GoalEvent {
    override val description = "Goal $goal failed"
}
