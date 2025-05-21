package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.tuprolog.core.Substitution

data class ExternalResponse(
    override val substitution: Substitution,
    override val feedback: ExecutionFeedback?,
    override val effects: Iterable<EnvironmentChange>,
) : ActionResponse<EnvironmentChange>
