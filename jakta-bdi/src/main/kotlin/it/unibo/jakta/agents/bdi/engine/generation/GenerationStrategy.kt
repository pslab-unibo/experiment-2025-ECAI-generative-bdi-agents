package it.unibo.jakta.agents.bdi.engine.generation

import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig

interface GenerationStrategy {
    val generator: Generator
    val generationConfig: GenerationConfig

    fun requestBlockingGeneration(generationState: GenerationState): GenerationResult

    fun initializeGeneration(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
        masID: MasID? = null,
        agentID: AgentID? = null,
        loggingConfig: LoggingConfig? = null,
    ): GenerationState

    fun updateGenerationConfig(generationConfig: GenerationConfig): GenerationStrategy
}
