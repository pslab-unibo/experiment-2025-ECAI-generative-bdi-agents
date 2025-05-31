package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.triggerFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NegativeFeedback")
sealed interface NegativeFeedback : ExecutionFeedback {
    @Serializable
    @SerialName("InapplicablePlan")
    data class InapplicablePlan(
        val plans: List<PlanApplicabilityResult>,
        override val description: String?,
    ) : NegativeFeedback {
        constructor(plans: List<PlanApplicabilityResult>) : this(
            plans,
            "The following plans are not applicable: " +
                plans.mapNotNull { it.trigger }.joinToString(", ") { triggerFormatter.format(it) },
        )
    }

    @Serializable
    @SerialName("PlanNotFound")
    data class PlanNotFound(
        val trigger: Trigger,
        override val description: String?,
    ) : NegativeFeedback {
        constructor(trigger: Trigger) : this(
            trigger,
            "No plan found for trigger ${triggerFormatter.format(trigger)}",
        )
    }
}
