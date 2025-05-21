package it.unibo.jakta.agents.bdi.engine.plangeneration.manager

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.impl.InvalidationStrategyImpl
import it.unibo.jakta.agents.bdi.engine.plans.PlanID

interface InvalidationStrategy {
    val logger: AgentLogger?

    fun invalidate(
        failedPlanID: PlanID,
        context: AgentContext,
    ): ExecutionResult

    companion object {
        fun of(logger: AgentLogger? = null): InvalidationStrategy = InvalidationStrategyImpl(logger)
    }
}
