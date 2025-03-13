package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.plans.impl.PlanImpl
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

interface Plan {
    val id: PlanID
    val trigger: Trigger
    val guard: Struct
    val goals: List<Goal>

    /** Determines if a plan is applicable */
    fun isApplicable(event: Event, beliefBase: BeliefBase): Boolean

    /** Determine if the plan is applicable and provide feedback */
    fun checkApplicability(event: Event, beliefBase: BeliefBase): PlanApplicabilityResult

    /** Returns the computed applicable plan */
    fun applicablePlan(event: Event, beliefBase: BeliefBase): Plan

    fun isRelevant(event: Event): Boolean

    fun toActivationRecord(): ActivationRecord

    companion object {
        fun of(id: PlanID? = null, trigger: Trigger, guard: Struct, goals: List<Goal>): Plan {
            val id = id ?: PlanID.of(trigger)
            return PlanImpl(id, trigger, guard, goals)
        }

        fun ofBeliefBaseAddition(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            id: PlanID? = null,
        ): Plan = of(id, BeliefBaseAddition(belief), guard, goals)

        fun ofBeliefBaseRemoval(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            id: PlanID? = null,
        ): Plan = of(id, BeliefBaseRemoval(belief), guard, goals)

        fun ofAchievementGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            id: PlanID? = null,
        ): Plan = of(id, AchievementGoalInvocation(value), guard, goals)

        fun ofAchievementGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            id: PlanID? = null,
        ): Plan = of(id, AchievementGoalFailure(value), guard, goals)

        fun ofTestGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            id: PlanID? = null,
        ): Plan = of(id, TestGoalInvocation(value), guard, goals)

        fun ofTestGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            id: PlanID? = null,
        ): Plan = of(id, TestGoalFailure(value), guard, goals)
    }
}
