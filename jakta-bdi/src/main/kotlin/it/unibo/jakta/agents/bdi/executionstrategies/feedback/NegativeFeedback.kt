package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter

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
        private val formattedTrigger = triggerFormatter.format(trigger)
        override val description = "No relevant plans found for the given trigger: $formattedTrigger"
    }
}
