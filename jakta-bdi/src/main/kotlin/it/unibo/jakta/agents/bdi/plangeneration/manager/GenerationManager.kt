package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GoalGenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationManagerImpl

interface GenerationManager {
    val logger: KLogger?
    val loggingConfig: LoggingConfig?
    val goalGenerationStrategy: GoalGenerationStrategy
    val goalTrackingStrategy: GoalTrackingStrategy
    val invalidationStrategy: InvalidationStrategy
    val unavailablePlanStrategy: UnavailablePlanStrategy

    companion object {
        fun of(
            logger: KLogger? = null,
            loggingConfig: LoggingConfig? = null,
            goalGenerationStrategy: GoalGenerationStrategy = GoalGenerationStrategy.of(
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
            goalGenerationStrategy,
            goalTrackingStrategy,
            invalidationStrategy,
            unavailablePlanStrategy,
        )

        fun getGoalsAchieved(
            intention: Intention,
            context: AgentContext,
        ): List<Goal> =
            if (intention is DeclarativeIntention) {
                context
                    .generationProcesses[intention.currentGeneratingPlan()]
                    ?.achievedGoalsHistory
                    ?: emptyList()
            } else {
                emptyList()
            }
    }
}
