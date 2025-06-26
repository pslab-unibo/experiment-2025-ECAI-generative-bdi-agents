package it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.PGPFailure.GenerationFailure
import it.unibo.jakta.agents.bdi.engine.generation.GenerationFailureResult
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.updaters.GenerationProcessUpdater
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger

internal class GenerationErrorHandler(
    val logger: AgentLogger?,
) {
    private val generationProcessUpdater = GenerationProcessUpdater(logger)

    fun handleUnknownResult(
        intention: Intention,
        context: AgentContext,
    ): ExecutionResult {
        val errorMsg = "Failed generation due to an unknown result"
        return ExecutionResult(
            context.copy(intentions = context.intentions.updateIntention(intention.pop())),
            feedback = GenerationFailure(errorMsg),
        )
    }

    fun handleFailure(
        intention: Intention,
        context: AgentContext,
        planGenResult: GenerationFailureResult,
    ): ExecutionResult {
        val errorMsg = "Failed generation due to: ${planGenResult.errorMsg}"
        val updatedGenProcesses = generationProcessUpdater.update(context, planGenResult)
        return ExecutionResult(
            context.copy(
                intentions = context.intentions.updateIntention(intention.pop()),
                generationProcesses = updatedGenProcesses,
            ),
            feedback = GenerationFailure(errorMsg),
        )
    }
}
