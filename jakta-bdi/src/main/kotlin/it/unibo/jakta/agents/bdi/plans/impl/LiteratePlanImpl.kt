package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct

internal data class LiteratePlanImpl(
    override val id: PlanID,
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
    override val literateTrigger: String?,
    override val literateGuard: String?,
    override val literateGoals: String?,
) : BasePlan(trigger, guard, goals), LiteratePlan {

    override fun applicablePlan(event: Event, beliefBase: BeliefBase): LiteratePlan =
        createApplicablePlan(event, beliefBase)?.let { (actualGuard, actualGoals) ->
            LiteratePlan.of(
                id,
                event.trigger,
                actualGuard,
                actualGoals,
                literateTrigger,
                literateGuard,
                literateGoals,
            )
        } ?: this
}
