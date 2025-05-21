package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.impl.GenerationResultBuilderImpl

interface GenerationResultBuilder {
    val logger: AgentLogger?

    fun buildResult(
        genGoal: GeneratePlan,
        context: AgentContext,
        intention: Intention,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult

    companion object {
        fun of(logger: AgentLogger? = null): GenerationResultBuilder = GenerationResultBuilderImpl(logger)
    }
}
