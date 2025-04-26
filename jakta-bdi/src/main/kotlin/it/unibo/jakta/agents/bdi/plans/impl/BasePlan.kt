package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.GuardFlattenerVisitor.Companion.flattenAnd
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

internal abstract class BasePlan(
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
) : Plan {
    override fun checkApplicability(event: Event, beliefBase: BeliefBase): PlanApplicabilityResult {
        return if (isRelevant(event)) {
            val mgu = event.trigger.value mguWith this.trigger.value
            val actualGuard = guard.apply(mgu).castToStruct()
            val guards = actualGuard.flattenAnd().associateWith { beliefBase.solve(it).isYes }
            PlanApplicabilityResult(event.trigger, guards)
        } else {
            PlanApplicabilityResult(
                error = "could not check applicability since the provided plan is not relevant",
            )
        }
    }

    override fun isApplicable(event: Event, beliefBase: BeliefBase): Boolean {
        val mgu = event.trigger.value mguWith this.trigger.value
        val actualGuard = guard.apply(mgu).castToStruct()
        return isRelevant(event) && beliefBase.solve(actualGuard).isYes
    }

    override fun isRelevant(event: Event): Boolean =
        event.trigger::class == this.trigger::class && (trigger.value mguWith event.trigger.value).isSuccess

    override fun toActivationRecord(): ActivationRecord = ActivationRecord.of(goals, id)

    protected fun createApplicablePlan(
        event: Event,
        beliefBase: BeliefBase,
        ignoreSource: Boolean = false,
    ): Pair<Struct, List<Goal>>? {
        return if (isApplicable(event, beliefBase)) {
            val mgu = event.trigger.value mguWith this.trigger.value
            val actualGuard = guard.apply(mgu).castToStruct()
            val solvedGuard = beliefBase.solve(actualGuard, ignoreSource)
            val actualGoals = goals.map {
                it.copy(
                    it.value
                        .apply(mgu)
                        .apply(solvedGuard.substitution)
                        .castToStruct(),
                )
            }
            Pair(actualGuard, actualGoals)
        } else {
            null
        }
    }
}
