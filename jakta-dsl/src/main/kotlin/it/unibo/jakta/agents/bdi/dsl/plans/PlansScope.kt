package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.events.AchievementGoalTrigger
import it.unibo.jakta.agents.bdi.events.BeliefBaseRevision
import it.unibo.jakta.agents.bdi.events.TestGoalTrigger
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleStructs
import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.jakta.agents.bdi.parsing.templates.TemplatePart
import it.unibo.jakta.agents.bdi.parsing.templates.impl.GoalTemplate
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.jakta.JaktaLogicProgrammingScope

class PlansScope(
    private val templates: List<LiteratePrologTemplate> = emptyList(),
) : Builder<Pair<Iterable<Plan>, List<GoalTemplate>>>,
    JaktaLogicProgrammingScope by JaktaLogicProgrammingScope.empty() {

    private val plans = mutableListOf<PlanScope>()
    private val planTemplates = mutableListOf<GoalTemplate>()

    fun achieve(goal: String): PlanScope {
        val template = GoalTemplate.of(goal)
        return if (template.parsedTemplate.filterIsInstance<TemplatePart.Argument>().isNotEmpty()) {
            planTemplates += template
            val parsedGoal = tangleStructs(goal, listOf(template)).firstOrNull()
            if (parsedGoal != null) {
                PlanScope(this, parsedGoal, AchievementGoalTrigger::class, goal, templates = templates + planTemplates)
            } else {
                achieve(atomOf(goal))
            }
        } else {
            val parsedGoal = tangleStructs(goal).firstOrNull()
            if (parsedGoal != null) {
                PlanScope(this, parsedGoal, AchievementGoalTrigger::class, goal, templates = templates + planTemplates)
            } else {
                achieve(atomOf(goal))
            }
        }
    }

    fun achieve(goal: Struct): PlanScope =
        PlanScope(this, goal, AchievementGoalTrigger::class, templates = templates)

    fun test(goal: String): PlanScope {
        val parsedGoal = tangleStructs(goal, templates).firstOrNull()
        return if (parsedGoal != null) {
            PlanScope(this, parsedGoal, TestGoalTrigger::class, goal, templates = templates)
        } else {
            test(atomOf(goal))
        }
    }

    fun test(goal: Struct): PlanScope = PlanScope(this, goal, TestGoalTrigger::class, templates = templates)

    operator fun String.unaryPlus(): PlanScope {
        val belief = tangleStructs(this, templates).firstOrNull() ?: atomOf(this)
        val planScope = PlanScope(
            this@PlansScope,
            belief,
            BeliefBaseRevision::class,
            failure = false,
            templates = templates,
        )
        plans += planScope
        return planScope
    }

    operator fun Struct.unaryPlus(): PlanScope {
        val planScope = PlanScope(
            this@PlansScope,
            this,
            BeliefBaseRevision::class,
            this.functor,
            failure = false,
            templates = templates,
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
        val belief = tangleStructs(this, templates).firstOrNull() ?: atomOf(this)
        val planScope = PlanScope(
            this@PlansScope,
            belief,
            BeliefBaseRevision::class,
            failure = true,
            templates = templates,
        )
        plans += planScope
        return planScope
    }

    operator fun Struct.unaryMinus(): PlanScope {
        val planScope = PlanScope(
            this@PlansScope,
            this,
            BeliefBaseRevision::class,
            this.functor,
            failure = true,
            templates = templates,
        )
        plans += planScope
        return planScope
    }

    operator fun PlanScope.unaryMinus(): PlanScope {
        val planScope = this.copy(failure = true)
        plans += planScope
        return planScope
    }

    override fun build(): Pair<Iterable<Plan>, List<GoalTemplate>> =
        Pair(plans.map { it.build() }, planTemplates)
}
