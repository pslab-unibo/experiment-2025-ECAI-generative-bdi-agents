package it.unibo.jakta.agents.bdi.engine.logging.loggers

import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.generation.PgpID

interface PGPLogger : JaktaLogger {
    val masID: MasID
    val agentID: AgentID
    val pgpID: PgpID
}
