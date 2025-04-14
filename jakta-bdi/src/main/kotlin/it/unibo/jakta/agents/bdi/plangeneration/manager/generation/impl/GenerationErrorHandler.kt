package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.FailureResult
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenericGenerationFailure
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GoalGenerationStrategy.Companion.MAX_GENERATION_ATTEMPTS

class GenerationErrorHandler {

    fun handleMaxConcurrentGenerationProcessesExceeded(
        intention: Intention,
        context: AgentContext,
    ): ExecutionResult {
        val errorMsg =
            "Cannot start a new generation process since the max amount " +
                "of $MAX_GENERATION_ATTEMPTS attempts exceeded."
        return ExecutionResult(
            context.copy(intentions = context.intentions.updateIntention(intention.pop())),
            feedback = GenericGenerationFailure(errorMsg),
        )
    }

    fun handleMissingGenerationStrategy(
        intention: Intention,
        context: AgentContext,
    ): ExecutionResult {
        val errorMsg = "Cannot generate new goals without a generation strategy"
        return ExecutionResult(
            context.copy(intentions = context.intentions.updateIntention(intention.pop())),
            feedback = GenericGenerationFailure(errorMsg),
        )
    }

    fun handleMaxAttemptsExceeded(
        intention: Intention,
        context: AgentContext,
        maxAttempts: Int,
    ): ExecutionResult {
        val errorMsg = "Reached maximum amount of retries ($maxAttempts attempts)"
        return ExecutionResult(
            context.copy(intentions = context.intentions.updateIntention(intention.pop())),
            feedback = GenericGenerationFailure(errorMsg),
        )
    }

    fun handleMissingDeclarativeIntention(
        intention: Intention,
        context: AgentContext,
    ): ExecutionResult {
        val errorMsg = "A running generation process needs a DeclarativeIntention to keep on going"
        return ExecutionResult(
            context.copy(intentions = context.intentions.updateIntention(intention.pop())),
            feedback = GenericGenerationFailure(errorMsg),
        )
    }

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
        planGenResult: FailureResult,
    ): ExecutionResult {
        val errorMsg = "Failed generation due to: ${planGenResult.errorMsg}"
        return ExecutionResult(
            context.copy(intentions = context.intentions.updateIntention(intention.pop())),
            feedback = GenericGenerationFailure(errorMsg),
        )
    }
}
