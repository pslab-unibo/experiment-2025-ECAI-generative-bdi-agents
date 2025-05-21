package it.unibo.jakta.agents.bdi.engine.actions.impl

import it.unibo.jakta.agents.bdi.engine.actions.Action
import it.unibo.jakta.agents.bdi.engine.actions.ActionRequest
import it.unibo.jakta.agents.bdi.engine.actions.ActionResponse
import it.unibo.jakta.agents.bdi.engine.actions.ActionSignature
import it.unibo.jakta.agents.bdi.engine.actions.effects.SideEffect
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Signature

abstract class AbstractAction<C : SideEffect, Res : ActionResponse<C>, Req : ActionRequest<C, Res>>(
    override val actionSignature: ActionSignature,
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

    companion object {
        /**
         * Extension function to convert a Signature to an ActionSignature
         */
        fun Signature.toActionSignature(parameterNames: List<String> = emptyList()): ActionSignature =
            ActionSignature(this, parameterNames)

        /**
         * Extension function to create an ActionSignature from a name and optional arity and parameter names
         */
        fun String.toActionSignature(
            arity: Int = 0,
            parameterNames: List<String> = emptyList(),
        ): ActionSignature = ActionSignature(Signature(this, arity), parameterNames)
    }
}
