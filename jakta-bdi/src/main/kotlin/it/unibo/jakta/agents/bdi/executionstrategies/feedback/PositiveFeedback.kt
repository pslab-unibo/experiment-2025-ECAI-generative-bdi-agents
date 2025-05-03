package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface PositiveFeedback : ExecutionFeedback {
    data class GenerationCompleted(
        val goal: GeneratePlan,
        val plans: List<Plan>,
        val admissibleGoals: Iterable<AdmissibleGoal>,
        val admissibleBeliefs: Iterable<AdmissibleBelief>,
    ) : PositiveFeedback {
        override val description = "Completed generation for goal ${goalFormatter.format(goal)}"

        override val metadata: Map<String, Any?> = super.metadata + buildMap {
            put("admissibleGoals", admissibleGoals)
            put("admissibleBeliefs", admissibleBeliefs)
            put("plans", plans)
        }
    }

    data class GenerationRequested(val goal: GeneratePlan) : PositiveFeedback {
        override val description = "Requested generation for goal ${goalFormatter.format(goal.goal)}"
    }
}
