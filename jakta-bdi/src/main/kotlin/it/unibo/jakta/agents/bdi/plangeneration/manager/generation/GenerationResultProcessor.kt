package it.unibo.jakta.agents.bdi.plangeneration.manager.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl.GenerationResultProcessorImpl
import it.unibo.jakta.agents.bdi.plans.PlanID

interface GenerationResultProcessor {
    val logger: KLogger?

    fun processResult(
        context: AgentContext,
        intention: DeclarativeIntention,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult

    companion object {
        fun of(logger: KLogger? = null) = GenerationResultProcessorImpl(logger)
    }
}
