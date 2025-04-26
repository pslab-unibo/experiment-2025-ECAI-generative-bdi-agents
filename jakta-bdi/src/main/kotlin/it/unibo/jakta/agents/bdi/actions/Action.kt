package it.unibo.jakta.agents.bdi.actions

import it.unibo.jakta.agents.bdi.Documentable
import it.unibo.jakta.agents.bdi.actions.effects.SideEffect
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.ExecutionFeedback
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Signature

interface Action<C : SideEffect, Res : ActionResponse<C>, Req : ActionRequest<C, Res>> : Documentable {
    override var purpose: String?
    val extendedSignature: ExtendedSignature
    val signature: Signature get() = extendedSignature.signature

    fun execute(request: Req): Res
    fun addResults(substitution: Substitution)
    fun addFeedback(feedback: ExecutionFeedback)
}
