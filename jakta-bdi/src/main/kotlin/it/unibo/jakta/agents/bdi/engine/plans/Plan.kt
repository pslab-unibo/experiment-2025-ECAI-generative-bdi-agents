package it.unibo.jakta.agents.bdi.engine.plans

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.engine.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.engine.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.impl.PlanImpl
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

interface Plan {
    val id: PlanID
    val trigger: Trigger
    val guard: Struct
    val goals: List<Goal>

    /** Determines if a plan is applicable */
    fun isApplicable(
        event: Event,
        beliefBase: BeliefBase,
    ): Boolean

    /** Determine if the plan is applicable and provide feedback */
    fun checkApplicability(
        event: Event,
        beliefBase: BeliefBase,
        ignoreSource: Boolean = false,
    ): PlanApplicabilityResult

    /** Returns the computed applicable plan */
    fun applicablePlan(
        event: Event,
        beliefBase: BeliefBase,
    ): Plan

    fun isRelevant(event: Event): Boolean

    fun toActivationRecord(): ActivationRecord

    companion object {
        fun of(
            id: PlanID,
            goals: List<Goal>,
        ): Plan = PlanImpl(id.trigger, id.guard, id, goals)

        fun of(
            id: PlanID? = null,
            trigger: Trigger,
            guard: Struct,
            goals: List<Goal>,
        ): Plan =
            PlanImpl(
                trigger,
                guard,
                id ?: PlanID(trigger, guard),
                goals,
            )

        fun ofBeliefBaseAddition(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
        ): Plan {
            val trigger = BeliefBaseAddition(belief)
            return of(PlanID(trigger, guard), trigger, guard, goals)
        }

        fun ofBeliefBaseRemoval(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
        ): Plan {
            val trigger = BeliefBaseRemoval(belief)
            return of(PlanID(trigger, guard), trigger, guard, goals)
        }

        fun ofAchievementGoalInvocation(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
        ): Plan {
            val trigger = AchievementGoalInvocation(value)
            return of(PlanID(trigger, guard), trigger, guard, goals)
        }

        fun ofAchievementGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
        ): Plan {
            val trigger = AchievementGoalFailure(value)
            return of(PlanID(trigger, guard), trigger, guard, goals)
        }

        fun ofTestGoalInvocation(
            belief: Belief,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
        ): Plan {
            val trigger = TestGoalInvocation(belief)
            return of(PlanID(trigger, guard), trigger, guard, goals)
        }

        fun ofTestGoalFailure(
            value: Struct,
            goals: List<Goal>,
            guard: Struct = Truth.TRUE,
        ): Plan {
            val trigger = TestGoalFailure(value)
            return of(PlanID(trigger, guard), trigger, guard, goals)
        }

        fun formatPlanToString(
            trigger: Trigger,
            guard: Struct,
            goals: List<Goal>,
            parentGenerationGoal: Any? = null,
        ): String =
            buildString {
                appendLine("Plan(")
                if (parentGenerationGoal != null) {
                    appendLine("  parentGenerationGoal = $parentGenerationGoal,")
                }
                appendLine("  trigger = $trigger,")
                appendLine("  guard = ${termFormatter.format(guard)},")
                appendLine("  goals = [")
                goals.forEach { goal ->
                    appendLine("    $goal,")
                }
                appendLine("  ]")
                append(")")
            }
    }
}
