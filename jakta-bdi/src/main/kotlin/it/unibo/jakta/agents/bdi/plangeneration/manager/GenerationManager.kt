package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GeneratePlanStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationManagerImpl

interface GenerationManager {
    val logger: KLogger?
    val loggingConfig: LoggingConfig?
    val planGenerationStrategy: GeneratePlanStrategy
    val goalTrackingStrategy: GoalTrackingStrategy
    val invalidationStrategy: InvalidationStrategy
    val unavailablePlanStrategy: UnavailablePlanStrategy

    companion object {
        fun of(
            logger: KLogger? = null,
            loggingConfig: LoggingConfig? = null,
            planGenerationStrategy: GeneratePlanStrategy = GeneratePlanStrategy.of(
                loggingConfig = loggingConfig,
                logger = logger,
            ),
            invalidationStrategy: InvalidationStrategy = InvalidationStrategy.of(logger),
            goalTrackingStrategy: GoalTrackingStrategy = GoalTrackingStrategy.of(invalidationStrategy, logger),
            unavailablePlanStrategy: UnavailablePlanStrategy =
                UnavailablePlanStrategy.of(invalidationStrategy, logger),
        ): GenerationManager = GenerationManagerImpl(
            logger,
            loggingConfig,
            planGenerationStrategy,
            goalTrackingStrategy,
            invalidationStrategy,
            unavailablePlanStrategy,
        )
    }
}
