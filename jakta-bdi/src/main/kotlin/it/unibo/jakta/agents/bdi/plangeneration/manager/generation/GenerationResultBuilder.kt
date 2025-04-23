package it.unibo.jakta.agents.bdi.plangeneration.manager.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl.GenerationResultBuilderImpl

interface GenerationResultBuilder {
    val logger: KLogger?

    fun buildResult(
        genGoal: GeneratePlan,
        context: AgentContext,
        intention: Intention,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult

    companion object {
        fun of(logger: KLogger? = null) = GenerationResultBuilderImpl(logger)
    }
}
