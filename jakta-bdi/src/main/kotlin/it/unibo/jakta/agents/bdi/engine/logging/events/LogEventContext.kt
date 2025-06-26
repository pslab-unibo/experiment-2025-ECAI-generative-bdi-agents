package it.unibo.jakta.agents.bdi.engine.logging.events

import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.generation.PgpID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LogEventContext")
data class LogEventContext(
    val event: LogEvent,
    val masID: MasID? = null,
    val agentID: AgentID? = null,
    val pgpID: PgpID? = null,
    val cycleCount: Long? = null,
)
