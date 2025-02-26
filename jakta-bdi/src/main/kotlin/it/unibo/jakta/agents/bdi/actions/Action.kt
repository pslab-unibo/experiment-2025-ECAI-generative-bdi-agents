package it.unibo.jakta.agents.bdi.actions

import it.unibo.jakta.agents.bdi.actions.effects.SideEffect
import it.unibo.tuprolog.core.Substitution

interface Action<C : SideEffect, Res : ActionResponse<C>, Req : ActionRequest<C, Res>> {
    val signature: LiterateSignature
    fun execute(request: Req): Res
    fun addResults(substitution: Substitution)
}
