package it.unibo.jakta.agents.bdi.plans.generated

import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

interface GeneratedPlan : Plan {
    val genCfg: GenerationConfiguration

    companion object {
        fun of(
            trigger: Trigger,
            guard: Struct,
            config: GenerationConfiguration,
            goals: List<Goal>,
        ): Plan = GeneratedPlanImpl(trigger, guard, goals, config)

        fun ofAchievementGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            config: GenerationConfiguration,
            guard: Struct = Truth.TRUE,
        ): Plan = of(AchievementGoalInvocation(value), guard, config, goals)

        fun ofAchievementGoalFailure(
            value: Struct,
            goals: List<Goal>,
            config: GenerationConfiguration,
            guard: Struct = Truth.TRUE,
        ): Plan = of(AchievementGoalFailure(value), guard, config, goals)
    }
}
