package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.actions.effects.AgentChange
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.tuprolog.core.Substitution

data class InternalResponse(
    override val substitution: Substitution,
    override val feedback: ExecutionFeedback?,
    override val effects: Iterable<AgentChange>,
) : ActionResponse<AgentChange>
