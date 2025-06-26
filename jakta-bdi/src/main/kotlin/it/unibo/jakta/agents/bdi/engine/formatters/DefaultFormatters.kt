package it.unibo.jakta.agents.bdi.engine.formatters

import it.unibo.jakta.agents.bdi.engine.Jakta.META_PLAN_BELIEF_FUNCTOR
import it.unibo.jakta.agents.bdi.engine.Jakta.capitalize
import it.unibo.jakta.agents.bdi.engine.Jakta.dropNumbers
import it.unibo.jakta.agents.bdi.engine.Jakta.operators
import it.unibo.jakta.agents.bdi.engine.Jakta.removeSource
import it.unibo.jakta.agents.bdi.engine.actions.Action
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalTrigger
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.engine.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.engine.events.BeliefBaseRevision
import it.unibo.jakta.agents.bdi.engine.events.BeliefBaseUpdate
import it.unibo.jakta.agents.bdi.engine.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.engine.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.TestGoalTrigger
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.goals.Achieve
import it.unibo.jakta.agents.bdi.engine.goals.Act
import it.unibo.jakta.agents.bdi.engine.goals.ActExternally
import it.unibo.jakta.agents.bdi.engine.goals.ActInternally
import it.unibo.jakta.agents.bdi.engine.goals.AddBelief
import it.unibo.jakta.agents.bdi.engine.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.engine.goals.Spawn
import it.unibo.jakta.agents.bdi.engine.goals.Test
import it.unibo.jakta.agents.bdi.engine.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.engine.goals.UpdateBelief
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.visitors.GuardFlattenerVisitor.Companion.flatten
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.operators.OperatorSet

object DefaultFormatters {
    val termFormatter: TermFormatter =
        TermFormatter.prettyExpressions(
            operatorSet = OperatorSet.DEFAULT + operators,
        )

    private fun getPrefix(goal: Goal): String =
        when (goal) {
            is GeneratePlan -> getPrefix(goal.goal)
            is TrackGoalExecution -> "track ${getPrefix(goal.goal)}"
            is Achieve -> "achieve"
            is Act -> "execute"
            is ActExternally -> "execute"
            is ActInternally -> "execute"
            is UpdateBelief -> "update"
            is AddBelief -> "add"
            is RemoveBelief -> "remove"
            is Spawn -> "spawn"
            is Test -> "test"
            is EmptyGoal -> "empty"
        }

    private fun Goal.getFormattableTerm() =
        when (this) {
            is Test -> value.removeSource()
            is UpdateBelief -> belief.removeSource()
            is AddBelief -> belief.removeSource()
            is RemoveBelief -> belief.removeSource()
            else -> value
        }

    val goalFormatter =
        Formatter<Goal> { goal ->
            "${getPrefix(goal)} ${termFormatter.format(goal.getFormattableTerm())}".dropNumbers()
        }

    val triggerFormatter =
        Formatter<Trigger> { trigger ->
            val goal = termFormatter.format(trigger.value)
            when (trigger) {
                is AchievementGoalInvocation -> "achieve $goal"
                is AchievementGoalFailure -> "achieve failure $goal"
                is TestGoalInvocation -> "test $goal"
                is TestGoalFailure -> "test failure $goal"
                is BeliefBaseAddition -> "belief addition $goal"
                is BeliefBaseRemoval -> "belief removal $goal"
                is BeliefBaseUpdate -> "belief update $goal"
                is AchievementGoalTrigger -> "achievement goal trigger from $goal"
                is BeliefBaseRevision -> "belief revision trigger from $goal"
                is TestGoalTrigger -> "test goal trigger from $goal"
            }.dropNumbers()
        }

    private fun <T> createFormatter(
        itemToString: (T) -> String?,
        purposeProvider: (T) -> String? = { null },
    ) = Formatter<T> { item ->
        itemToString(item)
            .let { base ->
                purposeProvider(item)?.let { "$base: $it" } ?: base
            }?.dropNumbers()
    }

    val beliefsFormatter =
        Formatter<Belief> { belief ->
            termFormatter
                .format(belief.rule.head.removeSource())
                .let { base -> belief.purpose?.let { "$base: $it" } ?: base }
                .takeUnless { it.contains(META_PLAN_BELIEF_FUNCTOR) }
                ?.dropNumbers()
        }

    val admissibleBeliefsFormatter =
        createFormatter(
            itemToString = { termFormatter.format(it.rule.head.removeSource()) },
            purposeProvider = AdmissibleBelief::purpose,
        )

    val admissibleGoalsFormatter =
        createFormatter<AdmissibleGoal>(
            itemToString = { triggerFormatter.format(it.trigger) },
            purposeProvider = { it.trigger.purpose },
        )

    val actionsFormatter =
        Formatter<Action<*, *, *>> { action ->
            buildString {
                val signature = action.actionSignature
                append(signature.name, "(")
                append(
                    signature.parameterNames
                        .takeIf { it.isNotEmpty() }
                        ?.joinToString { it.capitalize() }
                        ?: (1..signature.arity).joinToString { "Parameter$it" },
                )
                append(")")
                action.purpose?.let { append(": $it") }
            }.dropNumbers()
        }

    val planFormatter =
        Formatter<Plan> { plan ->
            buildString {
                plan.trigger.purpose?.let { append("% $it\n") }
                append(triggerFormatter.format(plan.trigger))
                append(" : ")
                append(
                    plan.guard.flatten().joinToString {
                        "\n${termFormatter.format(it).prependIndent("    ")}"
                    },
                )
                append(" <- ")
                append(
                    plan.goals.joinToString(";") {
                        "\n${goalFormatter.format(it)?.prependIndent("    ")}"
                    },
                )
            }
        }
}
