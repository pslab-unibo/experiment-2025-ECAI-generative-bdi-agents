package it.unibo.jakta.agents.bdi.executionstrategies

import it.unibo.jakta.agents.bdi.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.ExecutionFeedback

data class ExecutionResult(
    val newAgentContext: AgentContext,
    val feedback: ExecutionFeedback? = null,
    val environmentEffects: Iterable<EnvironmentChange> = listOf(),
)
