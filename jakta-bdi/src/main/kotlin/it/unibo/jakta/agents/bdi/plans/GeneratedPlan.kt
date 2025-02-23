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
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.impl.GeneratedPlanImpl
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

interface GeneratedPlan : LiteratePlan {
    val generationStrategy: GenerationStrategy?

    fun withGenerationStrategy(generationStrategy: GenerationStrategy): GeneratedPlan

    companion object {
        fun of(
            trigger: Trigger,
            guard: Struct,
            goals: List<Goal>,
            genStrategy: GenerationStrategy? = null,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): GeneratedPlan = GeneratedPlanImpl(
            trigger,
            guard,
            goals,
            genStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofBeliefBaseAddition(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            genStrategy: GenerationStrategy? = null,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): GeneratedPlan = of(
            BeliefBaseAddition(belief),
            guard,
            goals,
            genStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofBeliefBaseRemoval(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            genStrategy: GenerationStrategy? = null,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): GeneratedPlan = of(
            BeliefBaseRemoval(belief),
            guard,
            goals,
            genStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofAchievementGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.Companion.TRUE,
            genStrategy: GenerationStrategy? = null,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): GeneratedPlan = of(
            AchievementGoalInvocation(value),
            guard,
            goals,
            genStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofAchievementGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.Companion.TRUE,
            genStrategy: GenerationStrategy? = null,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): GeneratedPlan = of(
            AchievementGoalFailure(value),
            guard,
            goals,
            genStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofTestGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            genStrategy: GenerationStrategy? = null,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): GeneratedPlan = of(
            TestGoalInvocation(value),
            guard,
            goals,
            genStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )

        fun ofTestGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
            genStrategy: GenerationStrategy? = null,
            literateTrigger: String? = null,
            literateGuards: String? = null,
            literateGoals: String? = null,
        ): GeneratedPlan = of(
            TestGoalFailure(value),
            guard,
            goals,
            genStrategy,
            literateTrigger,
            literateGuards,
            literateGoals,
        )
    }
}
