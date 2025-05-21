package it.unibo.jakta.agents.bdi.engine.intentions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("IntentionID")
data class IntentionID(
    val id: String = generateId(),
) {
    companion object {
        private fun generateId(): String = UUID.randomUUID().toString()
    }
}
