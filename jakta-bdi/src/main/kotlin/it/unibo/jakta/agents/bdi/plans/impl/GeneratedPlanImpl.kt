package it.unibo.jakta.agents.bdi.plans.impl

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.PlanGenerationStepGoal
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.tuprolog.core.Struct

internal data class GeneratedPlanImpl(
    override val id: PlanID,
    override val trigger: Trigger,
    override val guard: Struct,
    override val goals: List<Goal>,
    override val generationStrategy: GenerationStrategy?,
    override val literateTrigger: String?,
    override val literateGuard: String?,
    override val literateGoals: String?,
) : BasePlan(trigger, guard, goals, id), GeneratedPlan {

    /**
     * In a [GeneratedPlan], intentions only pick goals of type [PlanGenerationStepGoal]
     */
    override fun toActivationRecord(): ActivationRecord =
        ActivationRecord.of(goals.filter { it is PlanGenerationStepGoal }, id)

    override fun applicablePlan(event: Event, beliefBase: BeliefBase): GeneratedPlan =
        createApplicablePlan(event, beliefBase)?.let { (actualGuard, actualGoals) ->
            GeneratedPlan.of(
                id,
                event.trigger,
                actualGuard,
                actualGoals,
                generationStrategy,
                literateTrigger,
                literateGuard,
                literateGoals,
            )
        } ?: this
}
