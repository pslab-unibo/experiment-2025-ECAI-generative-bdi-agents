package it.unibo.jakta.agents.bdi.plangeneration

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.PlanID

interface GenerationState {
    val goal: Generate
    val achievedGoalsHistory: List<Goal>
    val achievedGoalsBuffer: List<Goal>
    val rootPlanID: PlanID
    val logger: KLogger?
    val isGenerationFinished: Boolean
    val isGenerationEndConfirmed: Boolean
    val generationIteration: Int
    val failedGenerationRequests: Int

    fun reset(): GenerationState

    fun copy(
        goal: Generate = this.goal,
        achievedGoalsHistory: List<Goal> = this.achievedGoalsHistory,
        achievedGoalsBuffer: List<Goal> = this.achievedGoalsBuffer,
        rootPlanID: PlanID = this.rootPlanID,
        logger: KLogger? = this.logger,
        isGenerationFinished: Boolean = this.isGenerationFinished,
        isGenerationEndConfirmed: Boolean = this.isGenerationEndConfirmed,
        generationIteration: Int = this.generationIteration,
        failedGenerationRequests: Int = this.failedGenerationRequests,
    ): GenerationState
}
