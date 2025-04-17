package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.Jakta.toLeftNestedAnd
import it.unibo.jakta.agents.bdi.Prolog2Jakta
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleGoals
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleStructs
import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GuardFlatteningVisitor.Companion.flattenAnd
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanFactory
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import kotlin.collections.plus
import kotlin.reflect.KClass

data class PlanScope(
    private val scope: Scope,
    private val triggerStruct: Struct,
    private val triggerType: KClass<out Trigger>,
    private val triggerDescription: String? = null,
    private val failure: Boolean = false,
    private val templates: List<LiteratePrologTemplate> = emptyList(),
) {
    private val trigger = Trigger.fromStruct(triggerStruct, triggerType, failure)
    private var guard: Struct = Truth.TRUE
    private var literateGuard: String? = null
    private var generationStrategy: GenerationStrategy? = null
    private var goals: List<Goal> = mutableListOf()
    private var literateGoals: String? = null

    infix fun given(given: ConfigurationScope.() -> Unit): PlanScope {
        val planGenCfg = ConfigurationScope().also(given).build()
        generationStrategy = planGenCfg.generationStrategy
        return this
    }

    infix fun onlyIf(guards: GuardScope.() -> Struct): PlanScope {
        guard = GuardScope(scope).let(guards)
        guard = guard.accept(Prolog2Jakta).castToStruct()
        val res = guard.flattenAnd()
            .mapNotNull { tangleStructs(formatter.format(it), templates).firstOrNull() }
            .toLeftNestedAnd()
        res?.let { guard = it }
        return this
    }

    infix fun onlyIf(literateGuard: String): PlanScope {
        val litGuard = literateGuard.trimIndent()
        this.literateGuard = litGuard
        val parsedGuard = tangleStructs(litGuard, templates).toLeftNestedAnd()
        guard = parsedGuard ?: Truth.TRUE
        return this
    }

    infix fun then(body: BodyScope.() -> Unit): PlanScope {
        goals += BodyScope(scope, templates).also(body).build()
        return this
    }

    infix fun then(literateBody: String): PlanScope {
        literateGoals = literateBody
        tangleGoals(literateBody, templates).forEach { goal -> goals += goal }
        return this
    }

    fun build(): Plan =
        PlanFactory(
            trigger = trigger,
            goals = goals,
            guard = guard,
            generationStrategy = generationStrategy,
            parentPlanID = PlanID(trigger, guard),
            literateTrigger = triggerDescription,
            literateGuard = literateGuard,
            literateGoals = literateGoals,
        ).build()
}
