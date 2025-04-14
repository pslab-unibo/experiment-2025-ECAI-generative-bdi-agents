package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.impl.PartialPlanImpl
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

interface PartialPlan : LiteratePlan {
    /**
     * Keeps a reference to the plan that started the generation
     * process from which this plan was generated.
     *
     * Used when a generation process ends and the remaining
     * partial plans need to be eliminated or when feedback
     * for the applicability of a plan is given to the
     * corresponding generation process to which it belongs.
     */
    val parentPlanID: PlanID

    val generationStrategy: GenerationStrategy?

    companion object {
        fun of(
            id: PlanID? = null,
            trigger: Trigger,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            generationStrategy: GenerationStrategy? = null,
            parentPlanID: PlanID? = null,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
        ): PartialPlan =
            PartialPlanImpl(
                id ?: PlanID.of(trigger, guard),
                trigger,
                guard,
                goals,
                generationStrategy,
                parentPlanID ?: PlanID(trigger, guard),
                literateTrigger,
                literateGuard,
                literateGoals,
            )
    }
}

fun PartialPlan.copy(
    id: PlanID = this.id,
    trigger: Trigger = this.trigger,
    goals: List<Goal> = this.goals,
    guard: Struct = this.guard,
    generationStrategy: GenerationStrategy? = this.generationStrategy,
    parentPlanID: PlanID? = this.parentPlanID,
    literateTrigger: String? = this.literateTrigger,
    literateGuard: String? = this.literateGuard,
    literateGoals: String? = this.literateGoals,
): PartialPlan =
    PartialPlan.of(
        id,
        trigger,
        guard,
        goals,
        generationStrategy,
        parentPlanID,
        literateTrigger,
        literateGuard,
        literateGoals,
    )
