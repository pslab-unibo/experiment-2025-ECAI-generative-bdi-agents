package it.unibo.jakta.agents.bdi.plangeneration.feedback

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal

sealed interface FailureFeedback : ExecutionFeedback

data class InfiniteRecursion(
    val previousGoals: List<Goal>,
) : FailureFeedback {
    override val description = "Potential infinite recursion detected"
}

data class InapplicablePlan(
    val plans: List<PlanApplicabilityResult>,
) : FailureFeedback {
    override val description = "Found ${plans.size} inapplicable plans"

    override val metadata = super.metadata + buildMap {
        "inapplicablePlans" to plans
    }
}

class PlanNotFound(
    val trigger: Trigger,
) : FailureFeedback {
    override val description =
        "No relevant plans found for the given trigger: ${formatter.format(trigger.value)}"
}
