package it.unibo.jakta.agents.bdi.plans.feedback

import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.PlanExecutionTrackingGoal
import it.unibo.jakta.agents.bdi.logging.events.LogEvent

sealed interface GenerationFeedback : LogEvent {
    companion object {
        fun of(feedback: String): GenerationFeedback = StringFeedback(feedback)

        fun of(plans: List<PlanApplicabilityResult>): GenerationFeedback = PlanFeedback(plans)

        fun of(vararg goals: Goal): GenerationFeedback = GoalFeedback(goals.toList())
    }
}

data class StringFeedback(
    val message: String,
) : GenerationFeedback {
    override val description: String = "Feedback: $message"
}

data class PlanFeedback(
    val plans: List<PlanApplicabilityResult>,
) : GenerationFeedback {
    override val description: String = "Feedback: $plans"
}

data class GoalFeedback(
    val goals: List<Goal>,
) : GenerationFeedback {
    override val description = "Feedback: ${goals.filter {
        it !is Achieve && it !is PlanExecutionTrackingGoal
    }}"
}
