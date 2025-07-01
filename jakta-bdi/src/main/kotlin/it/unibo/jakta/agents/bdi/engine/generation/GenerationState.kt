package it.unibo.jakta.agents.bdi.engine.generation

import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.loggers.PGPLogger

interface GenerationState {
    val pgpID: PgpID
    val goal: GeneratePlan
    val logger: PGPLogger?

    fun copy(
        pgpID: PgpID = this.pgpID,
        goal: GeneratePlan = this.goal,
        logger: PGPLogger? = this.logger,
    ): GenerationState
}
