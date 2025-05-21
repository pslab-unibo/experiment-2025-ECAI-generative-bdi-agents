package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PGPSuccess")
sealed interface PGPSuccess : PositiveFeedback {
    @Serializable
    @SerialName("GenerationRequested")
    data class GenerationRequested(
        val generationStrategy: GenerationStrategy?,
        val goal: GeneratePlan,
        override val description: String,
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
        val admissibleGoals: Iterable<AdmissibleGoal>,
        val admissibleBeliefs: Iterable<AdmissibleBelief>,
        override val description: String,
    ) : PGPSuccess {
        constructor(
            goal: GeneratePlan,
            plans: List<Plan>,
            admissibleGoals: Iterable<AdmissibleGoal>,
            admissibleBeliefs: Iterable<AdmissibleBelief>,
        ) : this(
            goal,
            plans,
            admissibleGoals,
            admissibleBeliefs,
            "The goal ${goalFormatter.format(goal)} was successfully generated with the following plans: " +
                plans.joinToString(", ") { it.toString() },
        )
    }
}
