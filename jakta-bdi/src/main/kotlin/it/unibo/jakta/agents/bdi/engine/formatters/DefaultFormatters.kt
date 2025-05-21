package it.unibo.jakta.agents.bdi.engine.formatters

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
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.operators.OperatorSet

object DefaultFormatters {
    val termFormatter: TermFormatter =
        TermFormatter.prettyExpressions(
            operatorSet = OperatorSet.DEFAULT + operators,
        )

    val goalFormatter =
        object : Formatter<Goal> {
            override fun format(goal: Goal): String =
                when (goal) {
                    is EmptyGoal -> "<none>"
                    else -> {
                        val prefix =
                            when (goal) {
                                is Achieve -> "achieve"
                                is Act, is ActExternally, is ActInternally -> "execute"
                                is UpdateBelief -> "update"
                                is AddBelief -> "add"
                                is RemoveBelief -> "remove"
                                is Spawn -> "spawn"
                                is Test -> "test"
                                is GeneratePlan -> "generate"
                                is TrackGoalExecution -> "track"
                                else -> error("Unknown goal type: ${goal.javaClass.simpleName}")
                            }

                        val term =
                            when (goal) {
                                is UpdateBelief -> goal.belief.removeSource()
                                is AddBelief -> goal.belief.removeSource()
                                is RemoveBelief -> goal.belief.removeSource()
                                else -> goal.value
                            }

                        "$prefix ${termFormatter.format(term)}"
                    }
                }.dropNumbers()
        }

    val triggerFormatter =
        object : Formatter<Trigger> {
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
                }.dropNumbers()
            }
        }

    private fun <T> createFormatter(
        itemToString: (T) -> String,
        purposeProvider: (T) -> String?,
    ) = object : Formatter<T> {
        override fun format(item: T): String {
            val res = itemToString(item)
            val resWithPurpose = purposeProvider(item)?.let { "$res: $it" } ?: res
            return resWithPurpose.dropNumbers()
        }
    }

    val beliefsFormatter =
        object : Formatter<Belief> {
            private fun String.dropIfContainsSpecialBelief(): String =
                if (this.contains("missing_plan_for")) "" else this

            override fun format(item: Belief): String {
                val res = termFormatter.format(item.rule.head.removeSource())
                val resWithPurpose = item.purpose?.let { "$res: $it" } ?: res
                return resWithPurpose
                    .dropIfContainsSpecialBelief()
                    .dropNumbers()
            }
        }

    val admissibleBeliefsFormatter =
        createFormatter(
            { termFormatter.format(it.rule.head.removeSource()) },
            AdmissibleBelief::purpose,
        )

    val admissibleGoalsFormatter =
        createFormatter<AdmissibleGoal>(
            { triggerFormatter.format(it.trigger) },
            { it.trigger.purpose },
        )

    val actionsFormatter =
        object : Formatter<Action<*, *, *>> {
            override fun format(item: Action<*, *, *>): String =
                StringBuilder()
                    .apply {
                        val signature = item.actionSignature
                        append(signature.name)
                        val params = signature.parameterNames
                        append("(")
                        if (params.isNotEmpty()) {
                            append(params.joinToString { it.capitalize() })
                        } else {
                            (1..signature.arity).forEach { append("Parameter$it") }
                        }
                        append(")")
                        if (item.purpose != null) append(": ${item.purpose}")
                    }.toString()
                    .dropNumbers()
        }
}
