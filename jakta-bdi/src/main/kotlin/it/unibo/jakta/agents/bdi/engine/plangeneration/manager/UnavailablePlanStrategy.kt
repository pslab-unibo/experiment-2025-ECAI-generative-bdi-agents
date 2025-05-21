package it.unibo.jakta.agents.bdi.engine.plangeneration.manager

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.impl.UnavailablePlanStrategyImpl
import it.unibo.jakta.agents.bdi.engine.plans.Plan

interface UnavailablePlanStrategy {
    val logger: AgentLogger?
    val invalidationStrategy: InvalidationStrategy

    fun handleUnavailablePlans(
        selectedEvent: Event,
        relevantPlans: List<Plan>,
        isApplicablePlansEmpty: Boolean,
        context: AgentContext,
        generationStrategy: GenerationStrategy?,
    ): ExecutionResult

    fun handlePlanNotFound(
        selectedEvent: Event,
        context: AgentContext,
        generationStrategy: GenerationStrategy?,
    ): ExecutionResult

    companion object {
        fun of(
            invalidationStrategy: InvalidationStrategy,
            logger: AgentLogger? = null,
        ): UnavailablePlanStrategy = UnavailablePlanStrategyImpl(invalidationStrategy, logger)
    }
}
