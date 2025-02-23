package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.tuprolog.core.Struct

internal data class GeneratedPlanImpl(
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
    override val generationStrategy: GenerationStrategy? = null,
    override val literateTrigger: String? = null,
    override val literateGuards: String? = null,
    override val literateGoals: String? = null,
) : BasePlan(trigger, guard, goals), GeneratedPlan {

    override fun createConcretePlan(trigger: Trigger, guard: Struct, goals: List<Goal>): Plan {
        return GeneratedPlanImpl(
            trigger,
            guard,
            goals,
            generationStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )
    }

    override fun withGenerationStrategy(generationStrategy: GenerationStrategy): GeneratedPlan {
        return GeneratedPlanImpl(
            trigger,
            guard,
            goals,
            generationStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )
    }
}
