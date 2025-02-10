package it.unibo.jakta.agents.bdi.plans.generated

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.unify.Unificator.Companion.mguWith

internal data class GeneratedPlanImpl(
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
    override val genCfg: GenerationConfiguration,
) : GeneratedPlan {

    override fun isApplicable(event: Event, beliefBase: BeliefBase): Boolean {
        val mgu = event.trigger.value mguWith this.trigger.value
        val actualGuard = guard.apply(mgu).castToStruct()
        return isRelevant(event) && beliefBase.solve(actualGuard).isYes
    }

    override fun applicablePlan(event: Event, beliefBase: BeliefBase): GeneratedPlan =
        when (isApplicable(event, beliefBase)) {
            true -> {
                val mgu: Substitution = event.trigger.value mguWith this.trigger.value

                val updatedFunctor = event.trigger.value.functor.replace(Regex("<([^>]*)>")) {
                    val placeholderName = it.groupValues[1]
                    val substitution = mgu.filter { it.key.name == placeholderName }
                        .map { it.value }
                        .firstOrNull()
                    substitution?.toString() ?: placeholderName
                }

                val actualGuard = guard.apply(mgu).castToStruct()
                val solvedGuard = beliefBase.solve(actualGuard)
                val actualGoals = goals.map {
                    it.copy(
                        it.value
                            .apply(mgu)
                            .apply(solvedGuard.substitution)
                            .castToStruct(),
                    )
                }

                GeneratedPlanImpl(event.trigger, actualGuard, actualGoals, genCfg.withGoal(updatedFunctor))
            }
            else -> this
        }

    override fun isRelevant(event: Event): Boolean =
        event.trigger::class == this.trigger::class && (trigger.value mguWith event.trigger.value).isSuccess

    override fun toActivationRecord(): ActivationRecord = ActivationRecord.of(goals, trigger.value)
}
