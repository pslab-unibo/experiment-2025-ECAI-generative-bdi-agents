package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.triggerFormatter
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("PGPSuccess")
sealed interface PGPSuccess : PositiveFeedback {
    @Serializable
    @SerialName("GenerationRequested")
    data class GenerationRequested(
        @Transient
        val generationStrategy: GenerationStrategy? = null,
        val goal: GeneratePlan,
        override val description: String?,
    ) : PGPSuccess {
        constructor(generationStrategy: GenerationStrategy?, goal: GeneratePlan) : this(
            generationStrategy,
            goal,
            "Requested generation for goal ${goalFormatter.format(goal)}",
        )
    }

    @Serializable
    @SerialName("GenerationCompleted")
    data class GenerationCompleted(
        val goal: GeneratePlan,
        val plans: List<Plan>,
        val admissibleGoals: Set<AdmissibleGoal>,
        val admissibleBeliefs: Set<AdmissibleBelief>,
        override val description: String?,
    ) : PGPSuccess {
        constructor(
            goal: GeneratePlan,
            plans: List<Plan>,
            admissibleGoals: Set<AdmissibleGoal>,
            admissibleBeliefs: Set<AdmissibleBelief>,
        ) : this(
            goal,
            plans,
            admissibleGoals,
            admissibleBeliefs,
            "The goal ${goalFormatter.format(goal)} was successfully generated with the following plans: " +
                plans.mapNotNull { triggerFormatter.format(it.trigger) }.joinToString(", "),
        )
    }
}
