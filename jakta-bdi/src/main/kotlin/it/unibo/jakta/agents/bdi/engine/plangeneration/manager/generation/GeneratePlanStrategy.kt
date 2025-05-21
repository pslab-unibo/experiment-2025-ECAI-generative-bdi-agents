package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.impl.GeneratePlanStrategyImpl

interface GeneratePlanStrategy {
    val loggingConfig: LoggingConfig?
    val logger: AgentLogger?

    fun generatePlan(
        genGoal: GeneratePlan,
        intention: Intention,
        generationStrategy: GenerationStrategy,
        context: AgentContext,
        environment: Environment,
    ): ExecutionResult

    companion object {
        fun of(
            logger: AgentLogger? = null,
            loggingConfig: LoggingConfig? = null,
            genResProcessor: GenerationResultBuilder = GenerationResultBuilder.of(logger),
        ): GeneratePlanStrategy = GeneratePlanStrategyImpl(genResProcessor, loggingConfig, logger)
    }
}
