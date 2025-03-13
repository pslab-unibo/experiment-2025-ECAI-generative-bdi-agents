package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.impl.LiteratePlanImpl
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

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
            val id = id ?: PlanID.of(trigger)
            return LiteratePlanImpl(
                id,
                trigger,
                guard,
                goals,
                literateTrigger,
                literateGuard,
                literateGoals,
            )
        }

        fun ofBeliefBaseAddition(
            belief: Belief,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
            id: PlanID? = null,
        ): LiteratePlan = of(
            id,
            BeliefBaseAddition(belief),
            guard,
            goals,
            literateTrigger,
            literateGuard,
            literateGoals,
        )

        fun ofBeliefBaseRemoval(
            belief: Belief,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
            id: PlanID? = null,
        ): LiteratePlan = of(
            id,
            BeliefBaseRemoval(belief),
            guard,
            goals,
            literateTrigger,
            literateGuard,
            literateGoals,
        )

        fun ofAchievementGoalInvocation(
            value: Struct,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
            id: PlanID? = null,
        ): LiteratePlan = of(
            id,
            AchievementGoalInvocation(value),
            guard,
            goals,
            literateTrigger,
            literateGuard,
            literateGoals,
        )

        fun ofAchievementGoalFailure(
            value: Struct,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
            id: PlanID? = null,
        ): LiteratePlan = of(
            id,
            AchievementGoalFailure(value),
            guard,
            goals,
            literateTrigger,
            literateGuard,
            literateGoals,
        )

        fun ofTestGoalInvocation(
            value: Struct,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
            id: PlanID? = null,
        ): LiteratePlan = of(
            id,
            TestGoalInvocation(value),
            guard,
            goals,
            literateTrigger,
            literateGuard,
            literateGoals,
        )

        fun ofTestGoalFailure(
            value: Struct,
            guard: Struct = Truth.TRUE,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuard: String? = null,
            literateGoals: String? = null,
            id: PlanID? = null,
        ): LiteratePlan = of(
            id,
            TestGoalFailure(value),
            guard,
            goals,
            literateTrigger,
            literateGuard,
            literateGoals,
        )
    }
}
