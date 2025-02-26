package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.LiteratePrologParser.tanglePlanBody
import it.unibo.jakta.agents.bdi.LiteratePrologParser.tangleStruct
import it.unibo.jakta.agents.bdi.Prolog2Jakta
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import kotlin.reflect.KClass

data class PlanScope(
    private val scope: Scope,
    private val trigger: Struct,
    private val triggerType: KClass<out Trigger>,
    private val triggerDescription: String? = null,
) {
    private var guard: Struct = Truth.TRUE
    private var literateGuards: String? = null
    private var generate = false
    private var genStrategy: GenerationStrategy? = null
    private var goals: List<Goal> = mutableListOf()
    private var literateGoals: String? = null
    var failure: Boolean = false

    infix fun onlyIf(guards: GuardScope.() -> Struct): PlanScope {
        guard = GuardScope(scope).let(guards)
        guard = guard.accept(Prolog2Jakta).castToStruct()
        return this
    }

    infix fun onlyIf(literateGuard: String): PlanScope {
        val litGuard = literateGuard.trimIndent()
        literateGuards = litGuard
        val parsedGuard = tangleStruct(litGuard)
        guard = parsedGuard ?: Truth.TRUE
        return this
    }

    infix fun then(body: BodyScope.() -> Unit): PlanScope {
        goals += BodyScope(scope).also(body).build()
        return this
    }

    infix fun then(literateBody: String): PlanScope {
        literateGoals = literateBody
        val parsedGoals = tanglePlanBody(literateBody)
        parsedGoals.forEach { goal -> goals += goal }
        return this
    }

    infix fun given(given: PlanGenerationScope.() -> Unit): PlanScope {
        val planGenCfg = PlanGenerationScope().also(given).build()
        generate = planGenCfg.generate
        genStrategy = planGenCfg.generationStrategy
        return this
    }

    fun build(): Plan =
        PlanFactory(
            trigger = trigger,
            goals = goals,
            guard = guard,
            genStrategy = genStrategy,
            generate = generate,
            failure = failure,
            triggerDescription = triggerDescription,
            literateGuards = literateGuards,
            literateGoals = literateGoals,
            triggerType = triggerType,
        ).build()
}
