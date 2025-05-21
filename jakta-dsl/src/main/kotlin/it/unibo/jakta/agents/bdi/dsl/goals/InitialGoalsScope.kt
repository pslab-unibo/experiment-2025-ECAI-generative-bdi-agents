package it.unibo.jakta.agents.bdi.dsl.goals

import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.jakta.JaktaLogicProgrammingScope

class InitialGoalsScope :
    ScopeBuilder<Pair<Iterable<Trigger>, Set<AdmissibleGoal>>>,
    JaktaLogicProgrammingScope by JaktaLogicProgrammingScope.empty() {
    private val triggers = mutableListOf<Trigger>()
    private val admissibleGoals = mutableSetOf<AdmissibleGoal>()

    fun admissible(block: InitialGoalsScope.() -> Unit) {
        val scope = InitialGoalsScope()
        scope.block()
        admissibleGoals.addAll(
            scope.build().first.map {
                AdmissibleGoal(it)
            } + scope.build().second,
        )
    }

    operator fun Trigger.unaryPlus() {
        triggers += this
    }

    fun achieve(goal: Struct): Trigger = AchievementGoalInvocation(goal)

    fun achieve(goal: String): Trigger = AchievementGoalInvocation(atomOf(goal))

    fun test(belief: Belief): Trigger = TestGoalInvocation(belief)

    fun test(goal: Struct): Trigger = TestGoalInvocation(Belief.from(goal))

    fun test(goal: String): Trigger = TestGoalInvocation(Belief.from(atomOf(goal)))

    override fun build() = Pair(triggers.toList(), admissibleGoals)
}
