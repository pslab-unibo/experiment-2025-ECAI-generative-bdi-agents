package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.engine.Prolog2Jakta
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.generation.GenerationConfig
import it.unibo.jakta.agents.bdi.engine.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.PartialPlan
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import kotlin.reflect.KClass

data class PlanScope(
    private val scope: Scope,
    private val triggerStruct: Struct,
    private val triggerType: KClass<out Trigger>,
    private val triggerDescription: String? = null,
    private val failure: Boolean = false,
) {
    internal var trigger = Trigger.fromStruct(triggerStruct, triggerType, failure)
    private var guard: Struct = Truth.TRUE
    private var goals: List<Goal> = mutableListOf()
    var generationConfig: GenerationConfig? = null

    infix fun onlyIf(guards: GuardScope.() -> Struct): PlanScope {
        guard = GuardScope(scope).let(guards)
        guard = guard.accept(Prolog2Jakta).castToStruct()
        return this
    }

    infix fun then(body: BodyScope.() -> Unit): PlanScope {
        goals += BodyScope(scope, generationConfig).also(body).build()
        return this
    }

    fun build(): Plan =
        if (generationConfig != null) {
            PartialPlan.of(
                trigger = trigger,
                goals = goals.ifEmpty { listOf(EmptyGoal()) },
                guard = guard,
                generationConfig = generationConfig!!,
            )
        } else {
            Plan.of(
                trigger = trigger,
                goals = goals.ifEmpty { listOf(EmptyGoal()) },
                guard = guard,
            )
        }
}
