package it.unibo.jakta.agents.bdi.actions.impl

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.actions.ActionRequest
import it.unibo.jakta.agents.bdi.actions.ActionResponse
import it.unibo.jakta.agents.bdi.actions.ExtendedSignature
import it.unibo.jakta.agents.bdi.actions.effects.SideEffect
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.ExecutionFeedback
import it.unibo.tuprolog.core.Substitution

abstract class AbstractAction<C : SideEffect, Res : ActionResponse<C>, Req : ActionRequest<C, Res>> (
    override val extendedSignature: ExtendedSignature,
) : Action<C, Res, Req> {

    override var purpose: String? = null

    protected var result: Substitution = Substitution.empty()

    protected val effects: MutableList<C> = mutableListOf()

    protected var feedback: ExecutionFeedback? = null

    final override fun execute(request: Req): Res {
        if (request.arguments.size > signature.arity) {
            throw IllegalArgumentException("ERROR: Wrong number of arguments for action ${signature.name}")
        }
        action(request)
        val res = request.reply(result, feedback, effects.toMutableList())
        effects.clear()
        return res
    }

    override fun addResults(substitution: Substitution) {
        result = substitution
    }

    override fun addFeedback(feedback: ExecutionFeedback) {
        this.feedback = feedback
    }

    abstract fun action(request: Req)
}
