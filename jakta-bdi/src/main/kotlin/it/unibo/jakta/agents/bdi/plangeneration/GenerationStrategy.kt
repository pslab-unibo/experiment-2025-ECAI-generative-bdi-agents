package it.unibo.jakta.agents.bdi.plangeneration

import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.logging.LoggingConfig

interface GenerationStrategy {
    val generator: Generator

    fun requestBlockingGeneration(
        generationStrategy: GenerationStrategy,
        generationState: GenerationState,
    ): GenerationResult

    fun initializeGeneration(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
        loggingConfig: LoggingConfig? = null,
    ): GenerationState
}
