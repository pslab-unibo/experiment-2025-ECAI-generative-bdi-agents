package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.impl

import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.GenerationManager
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.GoalTrackingStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.UnavailablePlanStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.GeneratePlanStrategy

internal class GenerationManagerImpl(
    override val planGenerationStrategy: GeneratePlanStrategy,
    override val goalTrackingStrategy: GoalTrackingStrategy,
    override val invalidationStrategy: InvalidationStrategy,
    override val unavailablePlanStrategy: UnavailablePlanStrategy,
) : GenerationManager
