package it.unibo.jakta.agents.bdi.engine.plangeneration.manager

import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.GeneratePlanStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.impl.GenerationManagerImpl

interface GenerationManager {
    val planGenerationStrategy: GeneratePlanStrategy
    val goalTrackingStrategy: GoalTrackingStrategy
    val invalidationStrategy: InvalidationStrategy
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
                UnavailablePlanStrategy.of(invalidationStrategy, agentLogger),
        ): GenerationManager =
            GenerationManagerImpl(
                planGenerationStrategy,
                goalTrackingStrategy,
                invalidationStrategy,
                unavailablePlanStrategy,
            )
    }
}
