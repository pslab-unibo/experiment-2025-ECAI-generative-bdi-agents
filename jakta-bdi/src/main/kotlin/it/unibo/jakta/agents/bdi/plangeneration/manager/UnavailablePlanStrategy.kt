package it.unibo.jakta.agents.bdi.plangeneration.manager

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.UnavailablePlanStrategyImpl
import it.unibo.jakta.agents.bdi.plans.Plan

interface UnavailablePlanStrategy {
    val logger: KLogger?
    val invalidationStrategy: InvalidationStrategy

    fun handleUnavailablePlans(
        selectedEvent: Event,
        relevantPlans: List<Plan>,
        isApplicablePlansEmpty: Boolean,
        context: AgentContext,
        generationStrategy: GenerationStrategy?,
    ): ExecutionResult

    fun handlePlanNotFound(
        trigger: Trigger,
        context: AgentContext,
        generationStrategy: GenerationStrategy?,
    ): ExecutionResult

    companion object {
        fun of(
            invalidationStrategy: InvalidationStrategy,
            logger: KLogger? = null,
        ) = UnavailablePlanStrategyImpl(invalidationStrategy, logger)
    }
}
