package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.AchievementGoalTrigger
import it.unibo.jakta.agents.bdi.events.BeliefBaseAddition
import it.unibo.jakta.agents.bdi.events.BeliefBaseRemoval
import it.unibo.jakta.agents.bdi.events.BeliefBaseRevision
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.TestGoalTrigger
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.tuprolog.core.Struct
import kotlin.reflect.KClass

class PlanFactory(
    private val trigger: Struct,
    private val goals: List<Goal>,
    private val guard: Struct,
    private val genStrategy: GenerationStrategy?,
    private val generate: Boolean,
    private val failure: Boolean,
    private val triggerDescription: String?,
    private val literateGuards: String?,
    private val literateGoals: String?,
    private val triggerType: KClass<out Trigger>,
) {

    fun build(): Plan {
        val goalsList = goals.ifEmpty { listOf(EmptyGoal()) }
        val basicPlan = createPlan(goalsList)
        return when {
            genStrategy != null || generate -> {
                fromPlan(
                    basicPlan,
                    triggerDescription,
                    literateGuards,
                    literateGoals,
                )?.let { fromLiteratePlan(it, genStrategy) }
            }
            triggerDescription != null || literateGuards != null || literateGoals != null -> {
                fromPlan(
                    basicPlan,
                    triggerDescription,
                    literateGuards,
                    literateGoals,
                )
            }
            else -> basicPlan
        } ?: throw(IllegalArgumentException("Plan creation failed due to invalid trigger ${basicPlan.trigger}."))
    }

    private fun createPlan(goalsList: List<Goal>): Plan {
        return when (triggerType) {
            BeliefBaseRevision::class -> {
                if (failure) {
                    Plan.ofBeliefBaseRemoval(Belief.from(trigger), goalsList, guard)
                } else {
                    Plan.ofBeliefBaseAddition(Belief.from(trigger), goalsList, guard)
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

    companion object {
        fun isFailure(trigger: Trigger): Boolean? =
            when (trigger) {
                is BeliefBaseRemoval, is AchievementGoalFailure, is TestGoalFailure -> true
                is BeliefBaseAddition, is AchievementGoalInvocation, is TestGoalInvocation -> false
                else -> null
            }

        /**
         * Converts a [Plan] to a [LiteratePlan] by adding literate-specific parameters.
         */
        fun fromPlan(
            plan: Plan,
            triggerDescription: String?,
            literateGuards: String?,
            literateGoals: String?,
        ): LiteratePlan? =
            when (plan) {
                is LiteratePlan -> plan
                else -> {
                    val trigger = plan.trigger
                    val isFailure = isFailure(trigger)
                    if (isFailure == null) {
                        null
                    } else {
                        when (trigger) {
                            is BeliefBaseRevision -> {
                                if (isFailure) {
                                    LiteratePlan.ofBeliefBaseRemoval(
                                        Belief.from(trigger.value),
                                        plan.goals,
                                        plan.guard,
                                        triggerDescription,
                                        literateGuards,
                                        literateGoals,
                                    )
                                } else {
                                    LiteratePlan.ofBeliefBaseAddition(
                                        Belief.from(trigger.value),
                                        plan.goals,
                                        plan.guard,
                                        triggerDescription,
                                        literateGuards,
                                        literateGoals,
                                    )
                                }
                            }

                            is TestGoalTrigger -> {
                                if (isFailure) {
                                    LiteratePlan.ofTestGoalFailure(
                                        trigger.value,
                                        plan.goals,
                                        plan.guard,
                                        triggerDescription,
                                        literateGuards,
                                        literateGoals,
                                    )
                                } else {
                                    LiteratePlan.ofTestGoalInvocation(
                                        trigger.value,
                                        plan.goals,
                                        plan.guard,
                                        triggerDescription,
                                        literateGuards,
                                        literateGoals,
                                    )
                                }
                            }

                            is AchievementGoalTrigger -> {
                                if (isFailure) {
                                    LiteratePlan.ofAchievementGoalFailure(
                                        trigger.value,
                                        plan.goals,
                                        plan.guard,
                                        triggerDescription,
                                        literateGuards,
                                        literateGoals,
                                    )
                                } else {
                                    LiteratePlan.ofAchievementGoalInvocation(
                                        trigger.value,
                                        plan.goals,
                                        plan.guard,
                                        triggerDescription,
                                        literateGuards,
                                        literateGoals,
                                    )
                                }
                            }

                            else -> throw IllegalArgumentException("Unsupported trigger type: ${plan.trigger::class}")
                        }
                    }
                }
            }

        /**
         * Converts a [LiteratePlan] to a [GeneratedPlan] by adding generation-specific parameters.
         */
        fun fromLiteratePlan(
            literatePlan: LiteratePlan,
            genStrategy: GenerationStrategy?,
        ): GeneratedPlan? =
            when (literatePlan) {
                is GeneratedPlan -> literatePlan
                else -> {
                    val trigger = literatePlan.trigger
                    val isFailure = isFailure(trigger)
                    if (isFailure == null) {
                        null
                    } else {
                        when (trigger) {
                            is BeliefBaseRevision -> {
                                if (isFailure) {
                                    GeneratedPlan.ofBeliefBaseRemoval(
                                        Belief.from(trigger.value),
                                        literatePlan.goals,
                                        literatePlan.guard,
                                        genStrategy,
                                        literatePlan.literateTrigger,
                                        literatePlan.literateGuard,
                                        literatePlan.literateGoals,
                                    )
                                } else {
                                    GeneratedPlan.ofBeliefBaseAddition(
                                        Belief.from(trigger.value),
                                        literatePlan.goals,
                                        literatePlan.guard,
                                        genStrategy,
                                        literatePlan.literateTrigger,
                                        literatePlan.literateGuard,
                                        literatePlan.literateGoals,
                                    )
                                }
                            }

                            is TestGoalTrigger -> {
                                if (isFailure) {
                                    GeneratedPlan.ofTestGoalFailure(
                                        trigger.value,
                                        literatePlan.goals,
                                        literatePlan.guard,
                                        genStrategy,
                                        literatePlan.literateTrigger,
                                        literatePlan.literateGuard,
                                        literatePlan.literateGoals,
                                    )
                                } else {
                                    GeneratedPlan.ofTestGoalInvocation(
                                        trigger.value,
                                        literatePlan.goals,
                                        literatePlan.guard,
                                        genStrategy,
                                        literatePlan.literateTrigger,
                                        literatePlan.literateGuard,
                                        literatePlan.literateGoals,
                                    )
                                }
                            }

                            is AchievementGoalTrigger -> {
                                if (isFailure) {
                                    GeneratedPlan.ofAchievementGoalFailure(
                                        trigger.value,
                                        literatePlan.goals,
                                        literatePlan.guard,
                                        genStrategy,
                                        literatePlan.literateTrigger,
                                        literatePlan.literateGuard,
                                        literatePlan.literateGoals,
                                    )
                                } else {
                                    GeneratedPlan.ofAchievementGoalInvocation(
                                        trigger.value,
                                        literatePlan.goals,
                                        literatePlan.guard,
                                        genStrategy,
                                        literatePlan.literateTrigger,
                                        literatePlan.literateGuard,
                                        literatePlan.literateGoals,
                                    )
                                }
                            }

                            else -> throw IllegalArgumentException(
                                "Unsupported trigger type: ${literatePlan.trigger::class}",
                            )
                        }
                    }
                }
            }
    }
}
