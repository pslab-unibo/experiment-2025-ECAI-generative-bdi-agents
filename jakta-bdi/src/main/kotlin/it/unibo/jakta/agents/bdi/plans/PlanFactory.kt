package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.tuprolog.core.Struct

class PlanFactory(
    private val trigger: Trigger,
    private val goals: List<Goal>,
    private val guard: Struct,
    private val generationStrategy: GenerationStrategy? = null,
    private val parentPlanID: PlanID? = null,
    private val literateTrigger: String? = null,
    private val literateGuard: String? = null,
    private val literateGoals: String? = null,
) {

    private fun determinePlanType(basicPlan: Plan): Plan {
        return when {
            generationStrategy != null || basicPlan.goals.any { it is TrackGoalExecution || it is Generate } -> {
                PartialPlan.of(
                    basicPlan.id,
                    basicPlan.trigger,
                    basicPlan.guard,
                    basicPlan.goals,
                    generationStrategy,
                    parentPlanID,
                    literateTrigger,
                    literateGuard,
                    literateGoals,
                )
            }
            literateTrigger != null || literateGuard != null || literateGoals != null -> {
                LiteratePlan.of(
                    basicPlan.id,
                    basicPlan.trigger,
                    basicPlan.guard,
                    basicPlan.goals,
                    literateTrigger,
                    literateGuard,
                    literateGoals,
                )
            }
            else -> basicPlan
        } ?: throw IllegalArgumentException("Plan creation failed due to invalid trigger ${basicPlan.trigger}.")
    }

    fun build(): Plan {
        val goalsList = goals.ifEmpty { listOf(EmptyGoal()) }
        val basicPlan = Plan.of(PlanID.of(trigger, guard), trigger, guard, goalsList)
        return determinePlanType(basicPlan)
    }
}
