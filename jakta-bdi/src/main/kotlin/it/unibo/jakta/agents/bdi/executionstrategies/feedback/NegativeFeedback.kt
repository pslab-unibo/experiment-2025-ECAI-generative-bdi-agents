package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.events.Trigger

sealed interface NegativeFeedback : ExecutionFeedback {
    data class InapplicablePlan(
        val plans: List<PlanApplicabilityResult>,
    ) : NegativeFeedback {
        override val description = "Found ${plans.size} inapplicable plans"

        override val metadata = super.metadata + buildMap {
            "inapplicablePlans" to plans
        }
    }

    class PlanNotFound(
        val trigger: Trigger,
    ) : NegativeFeedback {
        override val description =
            "No relevant plans found for the given trigger: ${termFormatter.format(trigger.value)}"
    }
}
