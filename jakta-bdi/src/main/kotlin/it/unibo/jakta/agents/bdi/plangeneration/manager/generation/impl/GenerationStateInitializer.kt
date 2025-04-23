package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters.Updater

class GenerationStateInitializer(
    private val loggingConfig: LoggingConfig?,
    override val logger: KLogger?,
) : Updater {

    fun setupGenerationStart(
        context: AgentContext,
        generationStrategy: GenerationStrategy,
        initialGoal: GeneratePlan,
        environment: Environment,
    ): GenerationState =
        generationStrategy.initializeGeneration(
            initialGoal,
            context,
            environment.externalActions.values.toList(),
            loggingConfig,
        ).also {
            logger?.info { "Creating new generation process for goal: ${termFormatter.format(initialGoal.value)}" }
        }
}
