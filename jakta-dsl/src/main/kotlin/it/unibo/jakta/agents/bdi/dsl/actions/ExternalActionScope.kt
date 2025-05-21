package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.ExternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange

class ExternalActionScope(
    action: ExternalAction,
    request: ExternalRequest,
) : ActionScope<EnvironmentChange, ExternalResponse, ExternalRequest, ExternalAction>,
    ExternalAction by action,
    ExternalRequest by request
