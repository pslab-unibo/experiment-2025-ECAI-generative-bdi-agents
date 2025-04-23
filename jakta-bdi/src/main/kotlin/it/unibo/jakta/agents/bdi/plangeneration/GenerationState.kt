package it.unibo.jakta.agents.bdi.plangeneration

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal

interface GenerationState {
    val goal: GeneratePlan
    val achievedGoalsHistory: List<Goal>
    val consecutiveFailureCount: Int
    val logger: KLogger?

    fun copy(
        goal: GeneratePlan = this.goal,
        achievedGoalsHistory: List<Goal> = this.achievedGoalsHistory,
        consecutiveFailureCount: Int = this.consecutiveFailureCount,
        logger: KLogger? = this.logger,
    ): GenerationState
}
