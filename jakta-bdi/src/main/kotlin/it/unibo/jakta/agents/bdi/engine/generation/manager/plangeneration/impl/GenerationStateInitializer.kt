package it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.generation.GenerationState
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.updaters.Updater
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger

internal class GenerationStateInitializer(
    private val loggingConfig: LoggingConfig?,
    override val logger: AgentLogger?,
) : Updater {
    fun setupGenerationStart(
        context: AgentContext,
        generationStrategy: GenerationStrategy,
        initialGoal: GeneratePlan,
        environment: Environment,
    ): GenerationState =
        generationStrategy
            .initializeGeneration(
                initialGoal,
                context,
                environment.externalActions.values.toList(),
                logger?.masID,
                logger?.agentID,
                loggingConfig,
            )
}
