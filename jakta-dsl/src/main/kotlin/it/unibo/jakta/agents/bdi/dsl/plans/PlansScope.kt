package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.dsl.ScopeBuilder
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalTrigger
import it.unibo.jakta.agents.bdi.engine.events.BeliefBaseRevision
import it.unibo.jakta.agents.bdi.engine.events.TestGoalTrigger
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.jakta.JaktaLogicProgrammingScope

class PlansScope :
    ScopeBuilder<Iterable<Plan>>,
    JaktaLogicProgrammingScope by JaktaLogicProgrammingScope.empty() {
    private val plans = mutableListOf<PlanScope>()

    fun achieve(goal: String): PlanScope = achieve(atomOf(goal))

    fun achieve(goal: Struct): PlanScope = PlanScope(this, goal, AchievementGoalTrigger::class)

    fun test(goal: String): PlanScope = test(atomOf(goal))

    fun test(goal: Struct): PlanScope = PlanScope(this, goal, TestGoalTrigger::class)

    operator fun String.unaryPlus(): PlanScope {
        val planScope =
            PlanScope(
                this@PlansScope,
                atomOf(this),
                BeliefBaseRevision::class,
                failure = false,
            )
        plans += planScope
        return planScope
    }

    operator fun Struct.unaryPlus(): PlanScope {
        val planScope =
            PlanScope(
                this@PlansScope,
                this,
                BeliefBaseRevision::class,
                this.functor,
                failure = false,
            )
        plans += planScope
        return planScope
    }

    operator fun PlanScope.unaryPlus(): PlanScope {
        val planScope = this.copy(failure = false)
        plans += planScope
        return planScope
    }

    operator fun String.unaryMinus(): PlanScope {
        val planScope =
            PlanScope(
                this@PlansScope,
                atomOf(this),
                BeliefBaseRevision::class,
                failure = true,
            )
        plans += planScope
        return planScope
    }

    operator fun Struct.unaryMinus(): PlanScope {
        val planScope =
            PlanScope(
                this@PlansScope,
                this,
                BeliefBaseRevision::class,
                this.functor,
                failure = true,
            )
        plans += planScope
        return planScope
    }

    operator fun PlanScope.unaryMinus(): PlanScope {
        val planScope = this.copy(failure = true)
        plans += planScope
        return planScope
    }

    override fun build(): Iterable<Plan> = plans.map { it.build() }
}
