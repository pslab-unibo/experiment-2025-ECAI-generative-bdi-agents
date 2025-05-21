package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.InternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.AgentChange

class InternalActionsScope :
    AbstractActionsScope<AgentChange, InternalResponse, InternalRequest, InternalAction, InternalActionScope>() {
    public override fun newAction(
        name: String,
        arity: Int,
        parameterNames: List<String>,
        purpose: String?,
        f: InternalActionScope.() -> Unit,
    ): InternalAction =
        object : it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractInternalAction(name, arity) {
            override var purpose = purpose

            override fun action(request: InternalRequest) {
                InternalActionScope(this, request).f()
            }
        }
}
