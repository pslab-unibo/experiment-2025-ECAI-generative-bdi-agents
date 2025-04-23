package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface PositiveFeedback : ExecutionFeedback {
    data class GenerationCompleted(
        val goal: GeneratePlan,
        val plans: List<Plan>,
        val admissibleGoals: Set<AdmissibleGoal>,
        val admissibleBeliefs: Set<AdmissibleBelief>,
    ) : PositiveFeedback {
        override val description =
            "Completed generation for goal ${termFormatter.format(goal.value)}"

        override val metadata: Map<String, Any?> = super.metadata + buildMap {
            put("admissibleGoals", admissibleGoals)
            put("admissibleBeliefs", admissibleBeliefs)
            put("plans", plans)
        }
    }
}
