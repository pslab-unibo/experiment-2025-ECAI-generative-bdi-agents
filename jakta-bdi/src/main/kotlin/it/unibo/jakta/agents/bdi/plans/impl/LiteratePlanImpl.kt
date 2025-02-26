package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Struct

internal data class LiteratePlanImpl(
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
    override val literateTrigger: String?,
    override val literateGuard: String?,
    override val literateGoals: String?,
) : BasePlan(trigger, guard, goals), LiteratePlan {

    override fun createConcretePlan(trigger: Trigger, guard: Struct, goals: List<Goal>): Plan {
        return LiteratePlanImpl(
            trigger,
            guard,
            goals,
            literateTrigger,
            literateGuard,
            literateGoals,
        )
    }
}
