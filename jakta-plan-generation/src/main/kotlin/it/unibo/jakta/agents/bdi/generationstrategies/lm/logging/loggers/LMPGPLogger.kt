package it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.loggers

import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.addLogger
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.events.JaktaLogEvent
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.implementation
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.logger
import it.unibo.jakta.agents.bdi.engine.logging.loggers.PGPLogger
import it.unibo.jakta.agents.bdi.engine.logging.loggers.appenders.Appenders.buildAppenders
import it.unibo.jakta.agents.bdi.engine.plangeneration.PgpID
import org.apache.logging.log4j.Logger

class LMPGPLogger(
    override val masID: MasID,
    override val agentID: AgentID,
    override val pgpID: PgpID,
    override val logger: Logger,
) : PGPLogger {
    override fun log(event: () -> JaktaLogEvent) = logger.implementation(masID, event, agentID, pgpID)

    companion object {
        fun create(
            masID: MasID,
            agentID: AgentID,
            pgpID: PgpID,
            loggingConfig: LoggingConfig,
        ): LMPGPLogger {
            val name = "pgp-${pgpID.id}"
            val level = loggingConfig.logLevel
            val appenders = buildAppenders(name, loggingConfig)

            addLogger(name, level, appenders)
            val logger = logger(name)
            return LMPGPLogger(masID, agentID, pgpID, logger)
        }
    }
}
