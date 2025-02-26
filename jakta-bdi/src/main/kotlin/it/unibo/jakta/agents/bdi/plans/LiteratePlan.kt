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
            trigger: Trigger,
            guard: Struct,
            goals: List<Goal>,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan = LiteratePlanImpl(
            trigger,
            guard,
            goals,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofBeliefBaseAddition(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan = of(
            BeliefBaseAddition(belief),
            guard,
            goals,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofBeliefBaseRemoval(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan = of(
            BeliefBaseRemoval(belief),
            guard,
            goals,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofAchievementGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan = of(
            AchievementGoalInvocation(value),
            guard,
            goals,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofAchievementGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan = of(
            AchievementGoalFailure(value),
            guard,
            goals,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofTestGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan = of(
            TestGoalInvocation(value),
            guard,
            goals,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofTestGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): LiteratePlan = of(
            TestGoalFailure(value),
            guard,
            goals,
            literateTrigger,
            literateGuards,
            literateGoals,
        )
    }
}
