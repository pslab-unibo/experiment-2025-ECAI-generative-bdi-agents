package it.unibo.jakta.agents.bdi.logging.events

import it.unibo.jakta.agents.bdi.intentions.Intention

sealed interface IntentionEvent : LogEvent {
    val intention: Intention

    override val params: Map<String, Any?>
        get() = super.params + buildMap {
            put("plan", intention.currentPlan())
            put("nextGoal", intention.nextGoal())
            put("goalQueue", intention.recordStack)
        }
}

data class AssignPlanToNewIntention(
    override val intention: Intention,
) : IntentionEvent {
    override val description = "Created intention ${intention.id.id}"
}

data class AssignPlanToExistingIntention(
    override val intention: Intention,
) : IntentionEvent {
    override val description = "Updated intention ${intention.id.id}"
}

data class IntentionGoalRun(
    override val intention: Intention,
) : IntentionEvent {
    override val description: String = "Running intention ${intention.id.id}"
}
