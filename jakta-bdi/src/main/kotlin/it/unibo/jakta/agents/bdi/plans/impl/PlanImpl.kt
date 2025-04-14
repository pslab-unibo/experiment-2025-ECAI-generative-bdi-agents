package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct

internal data class PlanImpl(
    override val id: PlanID,
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
) : BasePlan(trigger, guard, goals) {

    override fun applicablePlan(event: Event, beliefBase: BeliefBase): Plan =
        createApplicablePlan(event, beliefBase)?.let { (actualGuard, actualGoals) ->
            Plan.of(id, event.trigger, actualGuard, actualGoals)
        } ?: this
}
