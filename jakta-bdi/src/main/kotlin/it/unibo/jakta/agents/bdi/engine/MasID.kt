package it.unibo.jakta.agents.bdi.engine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("MasID")
data class MasID(
    val id: String = generateId(),
) {
    companion object {
        private fun generateId(): String = UUID.randomUUID().toString()
    }
}
