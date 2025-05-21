package it.unibo.jakta.agents.bdi.engine.actions.impl

import it.unibo.jakta.agents.bdi.engine.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.ExternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term

internal data class ExternalRequestImpl(
    override val environment: Environment,
    override val sender: String,
    override val requestTimestamp: Time?,
    override val arguments: List<Term>,
) : ExternalRequest {
    override fun reply(
        substitution: Substitution,
        feedback: ExecutionFeedback?,
        effects: Iterable<EnvironmentChange>,
    ) = ExternalResponse(substitution, feedback, effects)

    override fun reply(
        substitution: Substitution,
        feedback: ExecutionFeedback?,
        vararg effects: EnvironmentChange,
    ) = reply(substitution, feedback, effects.asList())
}
