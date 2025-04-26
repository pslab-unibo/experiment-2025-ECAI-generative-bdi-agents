package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.Jakta.termFormatter
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
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.goals.Goal
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
        fun of(
            id: PlanID,
            goals: List<Goal>,
        ): Plan = PlanImpl(id, id.trigger, id.context, goals)

        fun of(
            id: PlanID? = null,
            trigger: Trigger,
            guard: Struct,
            goals: List<Goal>,
        ): Plan =
            PlanImpl(
                id ?: PlanID(trigger, guard),
                trigger,
                guard,
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
        ): String = buildString {
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
