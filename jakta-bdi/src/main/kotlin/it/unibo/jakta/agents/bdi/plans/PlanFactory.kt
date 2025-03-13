package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.events.AchievementGoalTrigger
import it.unibo.jakta.agents.bdi.events.BeliefBaseRevision
import it.unibo.jakta.agents.bdi.events.TestGoalTrigger
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.PlanGenerationStepGoal
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.tuprolog.core.Struct
import kotlin.reflect.KClass

class PlanFactory(
    private val trigger: Struct,
    private val goals: List<Goal>,
    private val guard: Struct,
    private val generationStrategy: GenerationStrategy?,
    private val generate: Boolean,
    private val failure: Boolean,
    private val literateTrigger: String?,
    private val literateGuard: String?,
    private val literateGoals: String?,
    private val triggerType: KClass<out Trigger>,
) {

    private fun determinePlanType(basicPlan: Plan): Plan {
        return when {
            generationStrategy != null || generate || basicPlan.goals.any { it is PlanGenerationStepGoal } -> {
                GeneratedPlan.of(
                    basicPlan.id,
                    basicPlan.trigger,
                    basicPlan.guard,
                    basicPlan.goals,
                    generationStrategy,
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
        val basicPlan = createPlan(goalsList)
        return determinePlanType(basicPlan)
    }

    private fun createPlan(goalsList: List<Goal>): Plan {
        return when (triggerType) {
            BeliefBaseRevision::class -> {
                if (failure) {
                    Plan.ofBeliefBaseRemoval(Belief.Companion.from(trigger), goalsList, guard)
                } else {
                    Plan.ofBeliefBaseAddition(Belief.Companion.from(trigger), goalsList, guard)
                }
            }
            TestGoalTrigger::class -> {
                if (failure) {
                    Plan.ofTestGoalFailure(trigger, goalsList, guard)
                } else {
                    Plan.ofTestGoalInvocation(trigger, goalsList, guard)
                }
            }
            AchievementGoalTrigger::class -> {
                if (failure) {
                    Plan.ofAchievementGoalFailure(trigger, goalsList, guard)
                } else {
                    Plan.ofAchievementGoalInvocation(trigger, goalsList, guard)
                }
            }
            else -> throw IllegalArgumentException("Unknown trigger type: $triggerType")
        }
    }
}
