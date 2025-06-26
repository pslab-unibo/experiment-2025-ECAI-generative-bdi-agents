package it.unibo.jakta.agents.bdi.engine.generation.manager

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.generation.manager.impl.UnavailablePlanStrategyImpl
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plans.Plan

interface UnavailablePlanStrategy {
    val logger: AgentLogger?

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
        fun of(logger: AgentLogger? = null): UnavailablePlanStrategy = UnavailablePlanStrategyImpl(logger)
    }
}
