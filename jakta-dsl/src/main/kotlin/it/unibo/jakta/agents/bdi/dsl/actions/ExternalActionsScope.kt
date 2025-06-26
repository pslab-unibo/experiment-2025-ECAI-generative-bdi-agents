package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.ExternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractExternalAction

class ExternalActionsScope :
    AbstractActionsScope<EnvironmentChange, ExternalResponse, ExternalRequest, ExternalAction, ExternalActionScope>() {
    public override fun newAction(
        name: String,
        arity: Int,
        purpose: String?,
        f: ExternalActionScope.() -> Unit,
    ): ExternalAction =
        object : AbstractExternalAction(name, arity) {
            override var purpose = purpose

            override fun action(request: ExternalRequest) {
                ExternalActionScope(this, request).f()
            }
        }

    public override fun newAction(
        name: String,
        parameterNames: List<String>,
        purpose: String?,
        f: ExternalActionScope.() -> Unit,
    ): ExternalAction =
        object : AbstractExternalAction(name, parameterNames) {
            override var purpose = purpose

            override fun action(request: ExternalRequest) {
                ExternalActionScope(this, request).f()
            }
        }
}
