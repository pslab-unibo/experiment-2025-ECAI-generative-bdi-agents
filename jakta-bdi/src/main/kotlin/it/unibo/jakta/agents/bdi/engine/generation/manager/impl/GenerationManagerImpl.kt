package it.unibo.jakta.agents.bdi.engine.generation.manager.impl

import it.unibo.jakta.agents.bdi.engine.generation.manager.GenerationManager
import it.unibo.jakta.agents.bdi.engine.generation.manager.GoalTrackingStrategy
import it.unibo.jakta.agents.bdi.engine.generation.manager.UnavailablePlanStrategy
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.GeneratePlanStrategy

internal class GenerationManagerImpl(
    override val planGenerationStrategy: GeneratePlanStrategy,
    override val goalTrackingStrategy: GoalTrackingStrategy,
    override val unavailablePlanStrategy: UnavailablePlanStrategy,
) : GenerationManager
