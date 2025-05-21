package it.unibo.jakta.agents.bdi.dsl.actions

import it.unibo.jakta.agents.bdi.engine.actions.Action
import it.unibo.jakta.agents.bdi.engine.actions.ActionRequest
import it.unibo.jakta.agents.bdi.engine.actions.ActionResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.SideEffect

interface ActionScope<C : SideEffect, Res : ActionResponse<C>, Req : ActionRequest<C, Res>, A : Action<C, Res, Req>> :
    ActionRequest<C, Res>,
    Action<C, Res, Req>
