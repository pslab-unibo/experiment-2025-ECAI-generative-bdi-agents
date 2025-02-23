package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.dsl.LiteratePrologParser
import it.unibo.jakta.agents.bdi.events.AchievementGoalTrigger
import it.unibo.jakta.agents.bdi.events.BeliefBaseRevision
import it.unibo.jakta.agents.bdi.events.TestGoalTrigger
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.jakta.JaktaLogicProgrammingScope

class PlansScope : Builder<Iterable<Plan>>, JaktaLogicProgrammingScope by JaktaLogicProgrammingScope.empty() {

    private val plans = mutableListOf<PlanScope>()

    fun achieve(goal: String): PlanScope = parseGoal(goal, ::achieve)

    fun achieve(goal: Struct, goalDescription: String = ""): PlanScope =
        PlanScope(this, goal, AchievementGoalTrigger::class, goalDescription)

    fun test(goal: String): PlanScope = parseGoal(goal, ::test)

    fun test(goal: Struct, goalDescription: String = ""): PlanScope =
        PlanScope(this, goal, TestGoalTrigger::class, goalDescription)

    private fun parseGoal(goal: String, action: (Struct, String) -> PlanScope): PlanScope {
        val parsedGoal = LiteratePrologParser.tangleStruct(goal)
        return if (parsedGoal != null) {
            action(parsedGoal, goal)
        } else {
            action(atomOf(goal), goal)
        }
    }

    operator fun String.unaryPlus(): PlanScope {
        val planScope = PlanScope(this@PlansScope, atomOf(this), BeliefBaseRevision::class)
        planScope.failure = false
        plans += planScope
        return planScope
    }

    operator fun Struct.unaryPlus(): PlanScope {
        val planScope = PlanScope(this@PlansScope, this, BeliefBaseRevision::class, this.functor)
        planScope.failure = false
        plans += planScope
        return planScope
    }

    operator fun PlanScope.unaryPlus(): PlanScope {
        failure = false
        plans += this
        return this
    }

    operator fun String.unaryMinus(): PlanScope {
        val planScope = PlanScope(this@PlansScope, atomOf(this), BeliefBaseRevision::class)
        planScope.failure = true
        plans += planScope
        return planScope
    }

    operator fun Struct.unaryMinus(): PlanScope {
        val planScope = PlanScope(this@PlansScope, this, BeliefBaseRevision::class, this.functor)
        planScope.failure = true
        plans += planScope
        return planScope
    }

    operator fun PlanScope.unaryMinus(): PlanScope {
        failure = true
        plans += this
        return this
    }

    override fun build(): Iterable<Plan> = plans.map { it.build() }
}
