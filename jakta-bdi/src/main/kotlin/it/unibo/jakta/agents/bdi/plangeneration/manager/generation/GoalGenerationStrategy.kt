package it.unibo.jakta.agents.bdi.plangeneration.manager.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl.GoalGenerationStrategyImpl

interface GoalGenerationStrategy {
    val loggingConfig: LoggingConfig?
    val logger: KLogger?

    fun generateGoal(
        genGoal: Generate,
        intention: Intention,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult

    companion object {
        const val MAX_GENERATION_ATTEMPTS = 3

        fun of(
            logger: KLogger? = null,
            loggingConfig: LoggingConfig? = null,
            genResProcessor: GenerationResultBuilder = GenerationResultBuilder.Companion.of(logger),
        ) = GoalGenerationStrategyImpl(genResProcessor, loggingConfig, logger)
    }
}
