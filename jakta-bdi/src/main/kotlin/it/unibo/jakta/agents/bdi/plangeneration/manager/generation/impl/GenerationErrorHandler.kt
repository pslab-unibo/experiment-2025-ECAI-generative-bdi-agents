package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.GenerationFailure.GenericGenerationFailure
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.GenerationFailureResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters.GenerationProcessUpdater

class GenerationErrorHandler(
    val logger: KLogger?,
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
        genGoal: GeneratePlan,
        intention: Intention,
        context: AgentContext,
        planGenResult: GenerationFailureResult,
    ): ExecutionResult {
        val errorMsg = "Failed generation due to: ${planGenResult.errorMsg}"
        val updatedGenProcesses = generationProcessUpdater.update(context, genGoal, planGenResult)
        return ExecutionResult(
            context.copy(
                intentions = context.intentions.updateIntention(intention.pop()),
                generationProcesses = updatedGenProcesses,
            ),
            feedback = GenericGenerationFailure(errorMsg),
        )
    }
}
