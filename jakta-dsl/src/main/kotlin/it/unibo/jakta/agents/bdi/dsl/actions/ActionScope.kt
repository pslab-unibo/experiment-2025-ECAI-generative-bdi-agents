package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.Action
import it.unibo.jakta.agents.bdi.engine.actions.ActionRequest
import it.unibo.jakta.agents.bdi.engine.actions.ActionResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.SideEffect

interface ActionScope<C : SideEffect, Res : ActionResponse<C>, Req : ActionRequest<C, Res>, A : Action<C, Res, Req>> :
    ActionRequest<C, Res>,
    Action<C, Res, Req> {
    val actionName get() =
        when {
            arguments.size == 1 -> {
                val arg = arguments[0].asAtom()?.value
                if (arg != null) {
                    "${actionSignature.name}($arg)"
                } else {
                    actionSignature.name
                }
            }
            arguments.isNotEmpty() -> {
                val args = arguments.joinToString(", ") { it.toString() }
                if (args.isNotEmpty()) {
                    "${actionSignature.name}($args)"
                } else {
                    actionSignature.name
                }
            }
            else -> actionSignature.name // For actions without arguments
        }
}
