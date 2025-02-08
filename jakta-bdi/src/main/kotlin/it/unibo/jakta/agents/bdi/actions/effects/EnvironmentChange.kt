package it.unibo.jakta.agents.bdi.actions.effects

import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.messages.Message

sealed interface EnvironmentChange : SideEffect

data class SpawnAgent(val agent: Agent) : EnvironmentChange {
    override val description = "Agent ${agent.name} has been spawned in the environment"
}

data class RemoveAgent(val agentName: String) : EnvironmentChange {
    override val description = "Agent $agentName has been removed from the environment"
}

data class SendMessage(
    val message: Message,
    val recipient: String,
) : EnvironmentChange {
    override val description =
        "Agent ${message.from} sent message ${message.type.javaClass.simpleName} to $recipient" +
            " with content: ${message.value}"
}

data class BroadcastMessage(val message: Message) : EnvironmentChange {
    override val description =
        "Agent ${message.from} broadcast message ${message.type.javaClass.simpleName}" +
            "\n\twith content: ${message.value}"
}

data class PopMessage(val agentName: String) : EnvironmentChange {
    override val description = "Popped a message from $agentName"
}

data class AddData(val key: String, val value: Any) : EnvironmentChange {
    override val description = "Key-value $key-$value has been added to the environment"
}

data class RemoveData(val key: String) : EnvironmentChange {
    override val description = "Key-value $key has been removed from the environment"
}

data class UpdateData(val newData: Map<String, Any>) : EnvironmentChange {
    override val description = "Key-values $newData have been added to the environment"
}
