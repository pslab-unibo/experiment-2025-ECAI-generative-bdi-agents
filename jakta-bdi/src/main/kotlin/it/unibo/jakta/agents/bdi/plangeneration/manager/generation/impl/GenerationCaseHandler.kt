package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention.Companion.toTrackingIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultProcessor
import it.unibo.jakta.agents.bdi.plans.PartialPlan

class GenerationCaseHandler(
    private val genResProcessor: GenerationResultProcessor,
) {

    fun handleNewRootGeneration(
        intention: Intention,
        context: AgentContext,
        plan: PartialPlan,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        val declarativeIntention = intention.toTrackingIntention()
            .copy(generatingPlans = listOf(plan.id))

        val result = genResProcessor.processResult(
            context,
            declarativeIntention,
            plan.id,
            planGenResult,
        )

        return result.copy(
            newAgentContext = result.newAgentContext.copy(
                intentions = IntentionPool.of(result.newAgentContext.intentions - declarativeIntention.id),
            ),
        )
    }

    fun handleNewChildGeneration(
        intention: DeclarativeIntention,
        context: AgentContext,
        plan: PartialPlan,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        val updatedIntention = intention.copy(
            generatingPlans = intention.generatingPlans + plan.id,
        )

        val result = genResProcessor.processResult(
            context,
            updatedIntention,
            plan.id,
            planGenResult,
        )

        return result.copy(
            newAgentContext = result.newAgentContext.copy(
                intentions = IntentionPool.of(result.newAgentContext.intentions - intention.id),
            ),
        )
    }

    fun handleExistingGeneration(
        intention: DeclarativeIntention,
        context: AgentContext,
        plan: PartialPlan,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult =
        genResProcessor.processResult(context, intention, plan.id, planGenResult)
}
