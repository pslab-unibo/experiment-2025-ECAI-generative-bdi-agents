package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.InternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.AgentChange
import it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractInternalAction

class InternalActionsScope :
    AbstractActionsScope<AgentChange, InternalResponse, InternalRequest, InternalAction, InternalActionScope>() {
    public override fun newAction(
        name: String,
        arity: Int,
        purpose: String?,
        f: InternalActionScope.() -> Unit,
    ): InternalAction =
        object : AbstractInternalAction(name, arity) {
            override var purpose = purpose

            override fun action(request: InternalRequest) {
                InternalActionScope(this, request).f()
            }
        }

    public override fun newAction(
        name: String,
        parameterNames: List<String>,
        purpose: String?,
        f: InternalActionScope.() -> Unit,
    ): InternalAction =
        object : AbstractInternalAction(name, parameterNames) {
            override var purpose = purpose

            override fun action(request: InternalRequest) {
                InternalActionScope(this, request).f()
            }
        }
}
