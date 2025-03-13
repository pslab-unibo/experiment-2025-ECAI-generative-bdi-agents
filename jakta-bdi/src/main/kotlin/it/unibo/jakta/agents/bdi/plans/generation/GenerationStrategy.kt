package it.unibo.jakta.agents.bdi.plans.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.feedback.GenerationFeedback

interface GenerationStrategy {
    val generationConfig: GenerationConfig
    val generationState: GenerationState
    val logger: KLogger?

    fun copy(logger: KLogger? = this.logger): GenerationStrategy

    fun requestPlanGeneration(
        generatedPlan: GeneratedPlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
    ): PlanGenerationResult

    fun provideGenerationFeedback(generationFeedback: GenerationFeedback): GenerationStrategy
}
