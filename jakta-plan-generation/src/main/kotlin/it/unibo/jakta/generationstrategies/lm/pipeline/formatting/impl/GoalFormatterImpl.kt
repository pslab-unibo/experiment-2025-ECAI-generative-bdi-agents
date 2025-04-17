package it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl

import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Act
import it.unibo.jakta.agents.bdi.goals.ActExternally
import it.unibo.jakta.agents.bdi.goals.ActInternally
import it.unibo.jakta.agents.bdi.goals.AddBelief
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.GoalFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.PromptFormatterImpl.Companion.formatWithAnd
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermFormatter

class GoalFormatterImpl(override val termFormatter: TermFormatter) : GoalFormatter {
    private fun formatGoals(goals: List<Struct>?, actionVerb: (List<Term>) -> String, action: String): String? {
        return goals?.takeIf { it.isNotEmpty() }?.let {
            "${it.formatWithAnd(termFormatter)} ${actionVerb(goals)} $action "
        }
    }

    override fun format(goals: List<Goal>, pastTense: Boolean, keepTriggerType: Boolean): List<String> {
        val goalMap = mutableMapOf<String, MutableList<Struct>>().apply {
            listOf(
                "Achieve", "Act", "ActExternally", "ActInternally", "AddBelief", "RemoveBelief",
                "UpdateBelief", "EmptyGoal", "Generate", "Spawn", "Test",
            ).forEach { key -> this[key] = mutableListOf() }
        }

        fun addGoal(key: String, name: String, value: Struct) {
            goalMap[key]?.add(if (keepTriggerType) Struct.of(name, value) else value)
        }

        goals.forEach { goal ->
            when (goal) {
                is Achieve -> addGoal("Achieve", "achieve", goal.value)
                is Act -> addGoal("Act", "execute", goal.value)
                is ActExternally -> addGoal("ActExternally", "execute", goal.value)
                is ActInternally -> addGoal("ActInternally", "execute", goal.value)
                is UpdateBelief -> {
                    val value = goal.belief.removeSource()
                    addGoal("UpdateBelief", "update", value)
                }
                is AddBelief -> {
                    val value = goal.belief.removeSource()
                    addGoal("AddBelief", "add", value)
                }
                is RemoveBelief -> {
                    val value = goal.belief.removeSource()
                    addGoal("RemoveBelief", "remove", value)
                }
                is Spawn -> addGoal("Spawn", "spawn", goal.value)
                is Test -> addGoal("Test", "test", goal.value)
                is EmptyGoal, is TrackGoalExecution -> {} // Ignore these goals
                is Generate -> addGoal("Generate", "generate", goal.value)
            }
        }

        val (haveVerb, becomeVerb) = if (pastTense) {
            Pair(
                PromptFormatterImpl.Companion.haveBeen,
                PromptFormatterImpl.Companion.became,
            )
        } else {
            Pair(PromptFormatterImpl.Companion.are, PromptFormatterImpl.Companion.become)
        }

        return listOfNotNull(
            formatGoals(goalMap["Achieve"], haveVerb, "achieved"),
            formatGoals(goalMap["Act"]?.plus(goalMap["ActInternally"] ?: emptyList()), haveVerb, "performed"),
            formatGoals(goalMap["AddBelief"]?.plus(goalMap["UpdateBelief"] ?: emptyList()), becomeVerb, "true"),
            formatGoals(goalMap["RemoveBelief"], becomeVerb, "false"),
            formatGoals(goalMap["Generate"], haveVerb, "generated"),
            formatGoals(goalMap["Spawn"], haveVerb, "spawned"),
            formatGoals(goalMap["Test"], haveVerb, "tested"),
        ).filter { it.isNotEmpty() }
    }
}
