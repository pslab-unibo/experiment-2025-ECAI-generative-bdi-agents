package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Struct

internal data class PlanImpl(
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
) : BasePlan(trigger, guard, goals) {

    override fun createConcretePlan(trigger: Trigger, guard: Struct, goals: List<Goal>): Plan {
        return PlanImpl(trigger, guard, goals)
    }
}
