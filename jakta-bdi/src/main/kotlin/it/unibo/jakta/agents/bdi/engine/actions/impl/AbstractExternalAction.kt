package it.unibo.jakta.agents.bdi.engine.actions.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.actions.ActionSignature
import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.ExternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.AddData
import it.unibo.jakta.agents.bdi.engine.actions.effects.BroadcastMessage
import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.RemoveAgent
import it.unibo.jakta.agents.bdi.engine.actions.effects.RemoveData
import it.unibo.jakta.agents.bdi.engine.actions.effects.SendMessage
import it.unibo.jakta.agents.bdi.engine.actions.effects.SpawnAgent
import it.unibo.jakta.agents.bdi.engine.actions.effects.UpdateData
import it.unibo.jakta.agents.bdi.engine.messages.Message

abstract class AbstractExternalAction(
    override val actionSignature: ActionSignature,
) : AbstractAction<EnvironmentChange, ExternalResponse, ExternalRequest>(actionSignature),
    ExternalAction {
    constructor(name: String) : this(name.toActionSignature())

    constructor(name: String, arity: Int) : this(name.toActionSignature(arity))

    constructor(name: String, vararg parameterNames: String) :
        this(name.toActionSignature(parameterNames.size, parameterNames.toList()))

    override fun addAgent(agent: Agent) {
        effects.add(SpawnAgent(agent))
    }

    override fun removeAgent(agentName: String) {
        effects.add(RemoveAgent(agentName))
    }

    override fun sendMessage(
        agentName: String,
        message: Message,
    ) {
        effects.add(SendMessage(message, agentName))
    }

    override fun broadcastMessage(message: Message) {
        effects.add(BroadcastMessage(message))
    }

    override fun addData(
        key: String,
        value: Any,
    ) {
        effects.add(AddData(key, value))
    }

    override fun removeData(key: String) {
        effects.add(RemoveData(key))
    }

    override fun updateData(newData: Map<String, Any>) {
        effects.add(UpdateData(newData))
    }

    override fun toString(): String = "ExternalAction(${signature.name}, ${signature.arity})"
}
