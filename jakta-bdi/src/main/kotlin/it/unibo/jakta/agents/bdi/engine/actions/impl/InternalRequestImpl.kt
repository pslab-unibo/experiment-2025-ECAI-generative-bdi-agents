package it.unibo.jakta.agents.bdi.engine.actions.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.actions.InternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.InternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.AgentChange
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term

internal data class InternalRequestImpl(
    override val agent: Agent,
    override val requestTimestamp: Time?,
    override val arguments: List<Term>,
) : InternalRequest {
    override fun reply(
        substitution: Substitution,
        feedback: ExecutionFeedback?,
        effects: Iterable<AgentChange>,
    ) = InternalResponse(substitution, feedback, effects)

    override fun reply(
        substitution: Substitution,
        feedback: ExecutionFeedback?,
        vararg effects: AgentChange,
    ) = reply(substitution, feedback, effects.asList())
}
