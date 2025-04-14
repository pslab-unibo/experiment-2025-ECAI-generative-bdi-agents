package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.manager.GenerationManager
import it.unibo.jakta.agents.bdi.plangeneration.manager.GoalTrackingStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.PlanTrackingStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.UnavailablePlanStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GoalGenerationStrategy

class GenerationManagerImpl(
    override val logger: KLogger?,
    override val loggingConfig: LoggingConfig?,
    override val goalGenerationStrategy: GoalGenerationStrategy,
    override val goalTrackingStrategy: GoalTrackingStrategy,
    override val planTrackingStrategy: PlanTrackingStrategy,
    override val invalidationStrategy: InvalidationStrategy,
    override val unavailablePlanStrategy: UnavailablePlanStrategy,
) : GenerationManager
