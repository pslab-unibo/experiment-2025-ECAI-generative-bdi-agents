package it.unibo.jakta.agents.bdi.plangeneration.manager.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl.GeneratePlanStrategyImpl

interface GeneratePlanStrategy {
    val loggingConfig: LoggingConfig?
    val logger: KLogger?

    fun generatePlan(
        genGoal: GeneratePlan,
        intention: Intention,
        generationStrategy: GenerationStrategy,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult

    companion object {
        fun of(
            logger: KLogger? = null,
            loggingConfig: LoggingConfig? = null,
            genResProcessor: GenerationResultBuilder = GenerationResultBuilder.of(logger),
        ) = GeneratePlanStrategyImpl(genResProcessor, loggingConfig, logger)
    }
}
