package it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl

import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.Jakta.wrapWithDelimiters
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ActionNotFound
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ActionSubstitutionFailure
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ExecutionFeedback
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenerationCompleted
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenerationStepExecuted
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenericGenerationFailure
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GoalExecutionSuccess
import it.unibo.jakta.agents.bdi.plangeneration.feedback.InapplicablePlan
import it.unibo.jakta.agents.bdi.plangeneration.feedback.InfiniteRecursion
import it.unibo.jakta.agents.bdi.plangeneration.feedback.InvalidActionArityError
import it.unibo.jakta.agents.bdi.plangeneration.feedback.PlanApplicabilityResult
import it.unibo.jakta.agents.bdi.plangeneration.feedback.PlanNotFound
import it.unibo.jakta.agents.bdi.plangeneration.feedback.TestGoalFailureFeedback
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.FeedbackFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PlanFormatter

class FeedbackFormatterImpl(
    override val planFormatter: PlanFormatter,
) : FeedbackFormatter {
    private val termFormatter = planFormatter.termFormatter
    private val goalFormatter = planFormatter.goalFormatter
    private val triggerFormatter = planFormatter.triggerFormatter

    override fun format(feedback: ExecutionFeedback): String =
        when (feedback) {
            is InapplicablePlan -> formatInapplicablePlans(feedback.plans)
            is GoalExecutionSuccess -> formatGoals(listOf(feedback.goalExecuted))
            is ActionNotFound -> feedback.description
            is InvalidActionArityError -> feedback.description
            is InfiniteRecursion -> formatInfiniteRecursion(feedback.previousGoals)
            is ActionSubstitutionFailure -> feedback.description
            is TestGoalFailureFeedback -> feedback.description
            is PlanNotFound -> feedback.description
            is GenerationCompleted -> formatCompletedPlan(feedback.basePlan, feedback.additionalPlans)
            is GenericGenerationFailure -> feedback.description
            is GenerationStepExecuted -> feedback.description
        }

    fun formatCompletedPlan(plan: Plan, additionalPlans: List<Plan>): String =
        StringBuilder().apply {
            appendLine("The plan generated to complete the given goals is:")
            appendLine()

            appendLine(planFormatter.formatPlan(plan))

            if (additionalPlans.isNotEmpty()) {
                appendLine()
                appendLine("the plans used by this plan are these:")

                append(additionalPlans.joinToString(separator = "\n", transform = planFormatter::formatPlan))
            }
        }.toString().lines().joinToString("\n") { it.trimStart() }

    fun formatInfiniteRecursion(goalsAchieved: List<Goal>) =
        StringBuilder().apply {
            appendLine("The following goals were achieved:")
            appendLine(
                goalFormatter.format(
                    goalsAchieved,
                    pastTense = true,
                    keepTriggerType = false,
                ).joinToString("\n"),
            )
            appendLine("before the execution terminated due to a potential infinite recursion.")
        }.toString()

    fun formatGoals(goals: List<Goal>): String {
        return if (goals.isEmpty()) {
            ""
        } else {
            StringBuilder().apply {
                appendLine("Done!")
                appendLine(
                    goalFormatter.format(
                        goals,
                        pastTense = true,
                        keepTriggerType = false,
                    ).joinToString("\n") { it.trim() }
                        .trim()
                        .trimIndent(),
                )
            }.toString()
        }
    }

    fun formatInapplicablePlans(plans: List<PlanApplicabilityResult>): String {
        return plans
            .filter { it.error == null && it.trigger != null && it.guards != null }
            .joinToString("\n") {
                StringBuilder().apply {
                    appendLine("${triggerFormatter.format(it.trigger!!)} cannot be chosen because:")
                    it.guards
                        ?.filterNot { it.value }
                        ?.forEach {
                            appendLine(
                                "${termFormatter.format(it.key.removeSource()).wrapWithDelimiters()} is false",
                            )
                        }

                    val trueGuards = it.guards?.filter { it.value }
                    if (trueGuards != null && trueGuards.isNotEmpty()) {
                        appendLine()
                        appendLine("That said:")
                        trueGuards.forEach {
                            appendLine(
                                "${termFormatter.format(it.key.removeSource()).wrapWithDelimiters()} is true",
                            )
                        }
                    }
                }.toString()
            }
    }
}
