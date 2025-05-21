package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.actions.effects.SideEffect
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.tuprolog.core.Substitution

interface ActionResponse<C : SideEffect> {
    val substitution: Substitution
    val effects: Iterable<C>
    val feedback: ExecutionFeedback?
}
