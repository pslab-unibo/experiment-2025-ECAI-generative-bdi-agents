package it.unibo.jakta.agents.bdi.engine.plans.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationConfig
import it.unibo.jakta.agents.bdi.engine.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.engine.plans.PartialPlan
import it.unibo.jakta.agents.bdi.engine.plans.Plan.Companion.formatPlanToString
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("PartialPlan")
internal class PartialPlanImpl(
    override val trigger: Trigger,
    override val guard: SerializableStruct,
    @Transient
    override val id: PlanID = PlanID(trigger, guard),
    override val goals: List<Goal>,
    override val parentGenerationGoal: GeneratePlan?,
    override val generationConfig: GenerationConfig?,
) : AbstractPlan(),
    PartialPlan {
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

    override fun isApplicable(
        event: Event,
        beliefBase: BeliefBase,
    ): Boolean {
        val mgu = event.trigger.value mguWith this.trigger.value
        val actualGuard = guard.apply(mgu).castToStruct()
        return isRelevant(event) && beliefBase.solve(actualGuard, ignoreSource = true).isYes
    }

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

    override fun applicablePlan(
        event: Event,
        beliefBase: BeliefBase,
    ): PartialPlan =
        createApplicablePlan(event, beliefBase, ignoreSource = true)?.let { (actualGuard, actualGoals) ->
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

    override fun toString(): String = formatPlanToString(trigger, guard, goals, parentGenerationGoal)
}
