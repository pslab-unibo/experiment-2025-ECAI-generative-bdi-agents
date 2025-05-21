package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.ExternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange

class ExternalActionsScope :
    AbstractActionsScope<EnvironmentChange, ExternalResponse, ExternalRequest, ExternalAction, ExternalActionScope>() {
    public override fun newAction(
        name: String,
        arity: Int,
        parameterNames: List<String>,
        purpose: String?,
        f: ExternalActionScope.() -> Unit,
    ): ExternalAction =
        object : it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractExternalAction(name, arity) {
            override var purpose = purpose

            override fun action(request: ExternalRequest) {
                ExternalActionScope(this, request).f()
            }
        }
}
