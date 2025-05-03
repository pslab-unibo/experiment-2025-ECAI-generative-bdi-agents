package it.unibo.jakta.agents.bdi.plangeneration

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.goals.GeneratePlan

interface GenerationState {
    val goal: GeneratePlan
    val logger: KLogger?

    fun copy(
        goal: GeneratePlan = this.goal,
        logger: KLogger? = this.logger,
    ): GenerationState
}
