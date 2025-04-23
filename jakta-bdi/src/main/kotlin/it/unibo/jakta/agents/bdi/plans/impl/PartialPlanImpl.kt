package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.plangeneration.GenerationConfig
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct

internal class PartialPlanImpl(
    override val id: PlanID,
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
    override val parentGenerationGoal: GeneratePlan?,
    override val generationConfig: GenerationConfig?,
) : BasePlan(trigger, guard, goals), PartialPlan {

    override fun copy(
        id: PlanID,
        trigger: Trigger,
        goals: List<Goal>,
        guard: Struct,
        parentGenerationGoal: GeneratePlan?,
    ): PartialPlan =
        PartialPlan.of(
            id,
            trigger,
            guard,
            goals,
            parentGenerationGoal,
        )

    override fun toActivationRecord(): ActivationRecord =
        ActivationRecord.of(
            /*
             * If the plan is partially unverified (with any [TrackGoal]), consider it as still generating.
             * Plans that have a [GeneratePlan] goal are considered complete so that
             * the other non-tracking goals are scheduled along with the [GeneratePlan].
             * Only when the [GeneratePlan] is executed and [TrackGoal]s start to be added
             * to the plan, then it becomes incomplete.
             */
            if (goals.any { it is TrackGoalExecution }) {
                goals.filter { it is TrackGoalExecution || it is GeneratePlan }
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
                parentGenerationGoal,
            )
        } ?: this

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PartialPlanImpl

        if (id != other.id) return false
        if (trigger != other.trigger) return false
        if (guard != other.guard) return false
        if (goals != other.goals) return false
        if (parentGenerationGoal != other.parentGenerationGoal) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + trigger.hashCode()
        result = 31 * result + guard.hashCode()
        result = 31 * result + goals.hashCode()
        result = 31 * result + parentGenerationGoal.hashCode()
        return result
    }

    override fun toString(): String {
        return "PartialPlanImpl(" +
            "id=$id, " +
            "trigger=$trigger, " +
            "guard=$guard, " +
            "goals=$goals, " +
            "parentGenerationGoal=$parentGenerationGoal)"
    }
}
