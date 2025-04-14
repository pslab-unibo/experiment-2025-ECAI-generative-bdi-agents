package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.TrackPlanExecution
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.PlanTrackingStrategyImpl

interface PlanTrackingStrategy {
    val logger: KLogger?

    fun trackPlanExecution(
        goal: TrackPlanExecution,
        intention: Intention,
        context: AgentContext,
    ): ExecutionResult

    companion object {
        fun of(logger: KLogger? = null) = PlanTrackingStrategyImpl(logger)
    }
}
