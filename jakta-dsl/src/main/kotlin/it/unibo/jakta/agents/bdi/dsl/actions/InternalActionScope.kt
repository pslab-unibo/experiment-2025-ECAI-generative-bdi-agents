package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.InternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.AgentChange

class InternalActionScope(
    action: InternalAction,
    request: InternalRequest,
) : ActionScope<AgentChange, InternalResponse, InternalRequest, InternalAction>,
    InternalAction by action,
    InternalRequest by request
