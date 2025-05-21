package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.actions.effects.SideEffect
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term

interface ActionRequest<C : SideEffect, Res : ActionResponse<C>> {
    val arguments: List<Term>
    val requestTimestamp: Time?

    fun reply(
        substitution: Substitution = Substitution.empty(),
        feedback: ExecutionFeedback?,
        effects: Iterable<C>,
    ): Res

    fun reply(
        substitution: Substitution = Substitution.empty(),
        feedback: ExecutionFeedback?,
        vararg effects: C,
    ): Res
}
