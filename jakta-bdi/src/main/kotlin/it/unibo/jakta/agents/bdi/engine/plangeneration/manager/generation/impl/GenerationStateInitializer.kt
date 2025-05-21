package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.updaters.Updater

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
            ).also {
                logger?.info { "Creating new generation process for goal: ${goalFormatter.format(initialGoal)}" }
            }
}
