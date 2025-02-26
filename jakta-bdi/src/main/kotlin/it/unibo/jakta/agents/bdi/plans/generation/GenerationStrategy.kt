package it.unibo.jakta.agents.bdi.plans.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan

interface GenerationStrategy {
    val genCfg: GenerationConfig
    val genState: GenerationState
    val logger: KLogger?

    fun copy(logger: KLogger? = null): GenerationStrategy

    fun requestPlanGeneration(
        generatedPlan: GeneratedPlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
    ): PlanGenerationResult
}
