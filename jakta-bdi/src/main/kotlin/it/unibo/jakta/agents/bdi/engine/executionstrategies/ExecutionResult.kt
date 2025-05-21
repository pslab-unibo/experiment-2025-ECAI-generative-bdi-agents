package it.unibo.jakta.agents.bdi.engine.executionstrategies

import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback

data class ExecutionResult(
    val newAgentContext: AgentContext,
    val feedback: ExecutionFeedback? = null,
    val environmentEffects: Iterable<EnvironmentChange> = listOf(),
)
