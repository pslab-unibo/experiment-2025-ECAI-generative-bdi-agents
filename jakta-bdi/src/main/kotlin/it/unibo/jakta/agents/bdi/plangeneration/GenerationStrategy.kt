package it.unibo.jakta.agents.bdi.plangeneration

import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ExecutionFeedback
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID

interface GenerationStrategy {
    val generator: Generator

    fun initializeGeneration(
        goal: Generate,
        rootPlanID: PlanID,
        context: AgentContext,
        externalActions: List<ExternalAction>,
        loggingConfig: LoggingConfig? = null,
    ): GenerationState

    fun requestBlockingGeneration(
        generatingPlan: PartialPlan,
        generationState: GenerationState,
    ): GenerationResult

    fun provideGenerationFeedback(
        generationState: GenerationState,
        executionFeedback: ExecutionFeedback,
    ): GenerationState

    fun checkGenerationEnded(
        goal: Generate,
        generationState: GenerationState,
        beliefBase: BeliefBase,
        generatedPlan: PartialPlan,
        additionalPlans: List<Plan>,
    ): GenerationState
}
