package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.TrackGoal
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct

internal data class PartialPlanImpl(
    override val id: PlanID,
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
    override val generationStrategy: GenerationStrategy?,
    override val parentPlanID: PlanID,
    override val literateTrigger: String?,
    override val literateGuard: String?,
    override val literateGoals: String?,
) : BasePlan(trigger, guard, goals), PartialPlan {

    /**
     * In a [PartialPlanImpl], intentions only pick goals of type [TrackGoal] or [Generate]
     */
    override fun toActivationRecord(): ActivationRecord =
        ActivationRecord.of(
            /*
             * If the plan is partially unverified (with any [TrackGoal]), consider it as still generating.
             * Plans that have a [Generate] goal are considered complete so that
             * the other non-tracking goals are scheduled along with the [Generate].
             * Only when the [Generate] is executed and [TrackGoal]s start to be added
             * to the plan, then it becomes incomplete.
             */
            if (goals.any { it is TrackGoal }) {
                goals.filter { it is TrackGoal || it is Generate }
            } else {
                goals
            },
            id,
        )

    override fun applicablePlan(event: Event, beliefBase: BeliefBase): PartialPlan =
        createApplicablePlan(event, beliefBase)?.let { (actualGuard, actualGoals) ->
            PartialPlan.of(
                id,
                event.trigger,
                actualGuard,
                actualGoals,
                generationStrategy,
                parentPlanID,
                literateTrigger,
                literateGuard,
                literateGoals,
            )
        } ?: this
}
