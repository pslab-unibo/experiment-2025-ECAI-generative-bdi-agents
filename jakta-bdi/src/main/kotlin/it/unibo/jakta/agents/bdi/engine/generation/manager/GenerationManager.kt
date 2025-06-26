package it.unibo.jakta.agents.bdi.engine.generation.manager

import it.unibo.jakta.agents.bdi.engine.generation.manager.impl.GenerationManagerImpl
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.GeneratePlanStrategy
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger

interface GenerationManager {
    val planGenerationStrategy: GeneratePlanStrategy
    val goalTrackingStrategy: GoalTrackingStrategy
    val unavailablePlanStrategy: UnavailablePlanStrategy

    companion object {
        fun of(
            agentLogger: AgentLogger? = null,
            loggingConfig: LoggingConfig? = null,
            planGenerationStrategy: GeneratePlanStrategy =
                GeneratePlanStrategy.of(
                    loggingConfig = loggingConfig,
                    logger = agentLogger,
                ),
            invalidationStrategy: InvalidationStrategy = InvalidationStrategy.of(agentLogger),
            goalTrackingStrategy: GoalTrackingStrategy = GoalTrackingStrategy.of(invalidationStrategy, agentLogger),
            unavailablePlanStrategy: UnavailablePlanStrategy =
                UnavailablePlanStrategy.of(agentLogger),
        ): GenerationManager =
            GenerationManagerImpl(
                planGenerationStrategy,
                goalTrackingStrategy,
                unavailablePlanStrategy,
            )
    }
}
