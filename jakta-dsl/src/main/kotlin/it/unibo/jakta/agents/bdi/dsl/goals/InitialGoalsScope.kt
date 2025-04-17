package it.unibo.jakta.agents.bdi.dsl.goals

import it.unibo.jakta.agents.bdi.Jakta.toLeftNestedAnd
import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleStructs
import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.jakta.JaktaLogicProgrammingScope

class InitialGoalsScope(
    private val templates: List<LiteratePrologTemplate> = emptyList(),
) : Builder<Iterable<Trigger>>, JaktaLogicProgrammingScope by JaktaLogicProgrammingScope.empty() {

    private val triggers = mutableListOf<Trigger>()

    fun achieve(goal: Struct) {
        triggers += AchievementGoalInvocation(goal)
    }

    fun achieve(goal: String) = parseGoal(goal, ::AchievementGoalInvocation)

    fun test(goal: Struct) {
        triggers += TestGoalInvocation(goal)
    }

    fun test(goal: String) = parseGoal(goal, ::TestGoalInvocation)

    private fun parseGoal(goal: String, invocation: (Struct) -> Trigger) {
        val parsedGoal = tangleStructs(goal, templates).toLeftNestedAnd() ?: atomOf(goal)
        triggers += invocation(parsedGoal)
    }

    override fun build(): Iterable<Trigger> = triggers.toList()
}
