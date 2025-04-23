package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.AchievementGoalTrigger
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.events.BeliefBaseRevision
import it.unibo.jakta.agents.bdi.events.BeliefBaseUpdate
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.TestGoalTrigger
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Act
import it.unibo.jakta.agents.bdi.goals.ActExternally
import it.unibo.jakta.agents.bdi.goals.ActInternally
import it.unibo.jakta.agents.bdi.goals.AddBelief
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.jakta.agents.bdi.plangeneration.GuardFlattenerVisitor.Companion.flattenAnd
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Truth

object Formatters {
    val goalFormatter = object : Formatter<Goal> {
        override fun format(goal: Goal): String =
            when (goal) {
                is Achieve -> "achieve ${termFormatter.format(goal.value)}"
                is Act -> "execute ${termFormatter.format(goal.value)}"
                is ActExternally, is ActInternally -> "execute ${goal.value}"
                is UpdateBelief -> "update ${goal.belief.removeSource()}"
                is AddBelief -> "add ${goal.belief.removeSource()}"
                is RemoveBelief -> "remove ${goal.belief.removeSource()}"
                is Spawn -> "spawn ${goal.value}"
                is Test -> "test ${goal.value}"
                is GeneratePlan -> "generate ${goal.value}"
                is TrackGoalExecution -> "track ${goal.value}"
                is EmptyGoal -> "<none>"
            }
    }

    val triggerFormatter = object : Formatter<Trigger> {
        override fun format(trigger: Trigger): String {
            val goal = termFormatter.format(trigger.value)
            return when (trigger) {
                is AchievementGoalInvocation -> "achieve $goal"
                is AchievementGoalFailure -> "achieve failure $goal"
                is TestGoalInvocation -> "test $goal"
                is TestGoalFailure -> "test failure $goal"
                is BeliefBaseAddition -> "belief addition $goal "
                is BeliefBaseRemoval -> "belief removal $goal"
                is BeliefBaseUpdate -> "belief update $goal"
                is AchievementGoalTrigger -> "achievement goal trigger from $goal"
                is BeliefBaseRevision -> "belief revision trigger from $goal"
                is TestGoalTrigger -> "test goal trigger from $goal"
            }
        }
    }

    val beliefsFormatter = object : Formatter<Belief> {
        override fun format(item: Belief): String =
            termFormatter.format(item.rule.head.removeSource())
    }

    val admissibleBeliefsFormatter = object : Formatter<AdmissibleBelief> {
        override fun format(item: AdmissibleBelief): String {
            val res = "${item.rule.head.removeSource()}"
            return item.purpose?.let { "$res: $it" } ?: res
        }
    }

    val planFormatter = object : Formatter<Plan> {
        override fun format(item: Plan): String = StringBuilder().apply {
            appendLine("```yaml")
            appendLine("EVENT: ${triggerFormatter.format(item.trigger)}")
            append("CONDITIONS:")
            if (item.guard == Truth.TRUE) {
                appendLine(" <none>")
            } else {
                append("\n")
                item.guard.flattenAnd().forEach {
                    appendLine("- ${termFormatter.format(it)}")
                }
            }
            append("OPERATIONS:")
            if (item.goals.size == 1 && item.goals.first() is EmptyGoal) {
                appendLine(" <none>")
            } else {
                append("\n")
                item.goals.forEach {
                    appendLine("- ${goalFormatter.format(it)}")
                }
            }
            appendLine("```")
        }.toString()
    }

    val admissibleGoalsFormatter = object : Formatter<AdmissibleGoal> {
        override fun format(item: AdmissibleGoal): String {
            val res = triggerFormatter.format(item.trigger)
            return item.trigger.purpose?.let { "$res: $it" } ?: res
        }
    }

    val actionsFormatter = object : Formatter<Action<*, *, *>> {
        override fun format(item: Action<*, *, *>): String = StringBuilder().apply {
            val signature = item.extendedSignature
            append(signature.name)
            val params = signature.parameterNames
            append("(")
            if (params.isNotEmpty()) {
                params.forEach { append((it.capitalize())) }
            } else {
                (1..signature.arity).forEach { append("Parameter$it") }
            }
            append(")")
            if (item.purpose != null) append(": ${item.purpose}")
        }.toString()
    }
}
