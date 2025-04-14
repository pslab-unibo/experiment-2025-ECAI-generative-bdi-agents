package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.InvalidationStrategyImpl

interface InvalidationStrategy {
    val logger: KLogger?

    fun invalidate(
        intention: DeclarativeIntention,
        context: AgentContext,
        isPotentialInfiniteRecursion: Boolean = false,
    ): ExecutionResult

    companion object {
        fun of(logger: KLogger? = null) = InvalidationStrategyImpl(logger)
    }
}
