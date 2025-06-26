package it.unibo.jakta.agents.bdi.engine.generation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
@SerialName("PgpID")
data class PgpID(
    val id: String = generateId(),
    val name: String = id,
) {
    companion object {
        private fun generateId(): String = UUID.randomUUID().toString()
    }

    override fun toString(): String = name
}
