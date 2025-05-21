package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.PGPFailure.GenericGenerationFailure
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationFailureResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.updaters.GenerationProcessUpdater

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
            feedback = GenericGenerationFailure(errorMsg),
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
            feedback = GenericGenerationFailure(errorMsg),
        )
    }
}
