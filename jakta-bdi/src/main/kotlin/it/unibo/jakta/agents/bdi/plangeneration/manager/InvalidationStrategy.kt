package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.InvalidationStrategyImpl
import it.unibo.jakta.agents.bdi.plans.PlanID

interface InvalidationStrategy {
    val logger: KLogger?

    fun reset(context: AgentContext): ExecutionResult

    fun invalidate(
        failedPlanID: PlanID,
        context: AgentContext,
    ): ExecutionResult

    companion object {
        const val MAX_ACHIEVED_GOALS = 200
        const val MAX_TIMES_FAILING = 3

        fun of(logger: KLogger? = null) = InvalidationStrategyImpl(logger)
    }
}
