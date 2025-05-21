package it.unibo.jakta.agents.bdi.engine.plangeneration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("PgpID")
data class PgpID(
    val id: String = generateId(),
) {
    companion object {
        private fun generateId(): String = UUID.randomUUID().toString()
    }
}
