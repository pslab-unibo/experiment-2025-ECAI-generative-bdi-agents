package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention.Companion.toTrackingIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenerationStepExecuted
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultBuilder
import it.unibo.jakta.agents.bdi.plans.PartialPlan

class GenerationCaseHandler(
    private val genResProcessor: GenerationResultBuilder,
) {

    fun handleNewRootGeneration(
        genGoal: Generate,
        intention: Intention,
        context: AgentContext,
        plan: PartialPlan,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        val declarativeIntention = intention.toTrackingIntention().copy(generatingPlans = listOf(plan.id))
        return genResProcessor.buildResult(context, declarativeIntention, plan.id, planGenResult).copy(
            feedback = if (planGenResult.generationState.isGenerationEndConfirmed) {
                GenerationStepExecuted("Terminated the generation process for goal ${formatter.format(genGoal.value)}")
            } else {
                GenerationStepExecuted(
                    "Completed start of a new generation process " +
                        "for goal ${formatter.format(genGoal.value)} as the root.",
                )
            },
        )
    }

    fun handleNewChildGeneration(
        genGoal: Generate,
        intention: DeclarativeIntention,
        context: AgentContext,
        plan: PartialPlan,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        val updatedIntention = intention.copy(generatingPlans = listOf(plan.id) + intention.generatingPlans)
        return genResProcessor.buildResult(context, updatedIntention, plan.id, planGenResult).copy(
            feedback = if (planGenResult.generationState.isGenerationEndConfirmed) {
                GenerationStepExecuted("Terminated the generation process for goal ${formatter.format(genGoal.value)}")
            } else {
                GenerationStepExecuted(
                    "Completed start of a new generation process " +
                        "for goal ${formatter.format(genGoal.value)} as a child.",
                )
            },
        )
    }

    fun handleExistingGeneration(
        genGoal: Generate,
        intention: DeclarativeIntention,
        context: AgentContext,
        plan: PartialPlan,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult =
        genResProcessor.buildResult(context, intention, plan.id, planGenResult).copy(
            feedback = if (planGenResult.generationState.isGenerationEndConfirmed) {
                GenerationStepExecuted("Terminated the generation process for goal ${formatter.format(genGoal.value)}")
            } else {
                GenerationStepExecuted(
                    "Continued the existing generation process for goal ${formatter.format(genGoal.value)}",
                )
            },
        )
}
