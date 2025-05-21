package it.unibo.jakta.agents.bdi.engine.plangeneration

import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.loggers.PGPLogger

interface GenerationState {
    val goal: GeneratePlan
    val logger: PGPLogger?

    fun copy(
        goal: GeneratePlan = this.goal,
        logger: PGPLogger? = this.logger,
    ): GenerationState
}
