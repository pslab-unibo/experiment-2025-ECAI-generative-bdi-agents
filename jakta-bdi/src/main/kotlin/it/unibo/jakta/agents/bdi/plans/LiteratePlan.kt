package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.impl.LiteratePlanImpl
import it.unibo.tuprolog.core.Struct

interface LiteratePlan : Plan {
    val literateTrigger: String?
    val literateGuard: String?
    val literateGoals: String?

    companion object {
        fun of(
            id: PlanID? = null,
            trigger: Trigger,
            guard: Struct,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan {
            return LiteratePlanImpl(
                id ?: PlanID.of(trigger, guard),
                trigger,
                guard,
                goals,
                literateTrigger,
                literateGuard,
                literateGoals,
            )
        }
    }
}
