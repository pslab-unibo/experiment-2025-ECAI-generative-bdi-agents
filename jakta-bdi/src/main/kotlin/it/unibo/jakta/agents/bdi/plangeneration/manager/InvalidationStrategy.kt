package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.InvalidationStrategyImpl
import it.unibo.jakta.agents.bdi.plans.PlanID

interface InvalidationStrategy {
    val logger: KLogger?

    fun invalidate(
        failedPlanID: PlanID,
        context: AgentContext,
    ): ExecutionResult

    companion object {
        fun of(logger: KLogger? = null) = InvalidationStrategyImpl(logger)
    }
}
