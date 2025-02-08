package it.unibo.jakta.agents.bdi.logging

import it.unibo.jakta.agents.bdi.intentions.Intention

sealed interface IntentionEvent : LogEvent

data class AssignPlanToNewIntention(
    val intention: Intention,
) : IntentionEvent {
    override val description = "Intention ${intention.recordStack}"
//        "Assigned ${intention.currentPlan()} to new intention ${intention.id.id}"
}

data class AssignPlanToExistingIntention(
    val intention: Intention,
) : IntentionEvent {
    override val description = "Intention ${intention.recordStack}"
    // "Assigned ${intention.currentPlan()} to existing intention ${intention.id.id}"
}

//    data class IntentionWaiting(
//        val intention: Intention,
//    ): IntentionEvent {
//        override val description = intentionDescription(intention, "waiting")
//    }
//
//    data class IntentionSuspended(
//        val intention: Intention,
//    ): IntentionEvent {
//        override val description = intentionDescription(intention, "suspended")
//    }

data class IntentionGoalRun(
    val intention: Intention,
) : IntentionEvent {
    override val description: String = "Running next goal ${intention.nextGoal()} of intention" +
        " with plan: ${intention.currentPlan()}" +
        " with id: ${intention.id.id}"
}
