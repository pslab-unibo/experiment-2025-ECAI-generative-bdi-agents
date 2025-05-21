package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.Documentable
import it.unibo.jakta.agents.bdi.engine.actions.effects.SideEffect
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ExecutionFeedback
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Signature

interface Action<C : SideEffect, Res : ActionResponse<C>, Req : ActionRequest<C, Res>> : Documentable {
    override var purpose: String?
    val actionSignature: ActionSignature
    val signature: Signature get() = actionSignature.signature

    fun execute(request: Req): Res

    fun addResults(substitution: Substitution)

    fun addFeedback(feedback: ExecutionFeedback)
}
