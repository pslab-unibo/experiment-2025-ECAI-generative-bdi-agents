package it.unibo.jakta.agents.bdi.engine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("AgentID")
data class AgentID(
    val id: String = generateId(),
) {
    companion object {
        private fun generateId(): String = UUID.randomUUID().toString()
    }
}
