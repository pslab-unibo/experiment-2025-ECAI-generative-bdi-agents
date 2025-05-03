package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plangeneration.GenerationConfig
import it.unibo.jakta.agents.bdi.plans.impl.PartialPlanImpl
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

interface PartialPlan : Plan {
    val parentGenerationGoal: GeneratePlan?

    val generationConfig: GenerationConfig?

    companion object {
        fun of(
            id: PlanID,
            goals: List<Goal>,
            parentGenerationGoal: GeneratePlan? = null,
            generationConfig: GenerationConfig? = null,
        ): PartialPlan =
            PartialPlanImpl(
                id,
                id.trigger,
                id.guard,
                goals,
                parentGenerationGoal,
                generationConfig,
            )

        fun of(
            id: PlanID? = null,
            trigger: Trigger,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            parentGenerationGoal: GeneratePlan? = null,
            generationConfig: GenerationConfig? = null,
        ): PartialPlan =
            PartialPlanImpl(
                id ?: PlanID(trigger, guard),
                trigger,
                guard,
                goals,
                parentGenerationGoal,
                generationConfig,
            )
    }

    fun copy(
        id: PlanID = this.id,
        trigger: Trigger = this.trigger,
        goals: List<Goal> = this.goals,
        guard: Struct = this.guard,
        parentGenerationGoal: GeneratePlan? = this.parentGenerationGoal,
    ): PartialPlan
}
