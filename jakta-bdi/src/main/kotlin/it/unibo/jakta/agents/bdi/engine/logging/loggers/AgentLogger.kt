package it.unibo.jakta.agents.bdi.engine.logging.loggers

import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig.Companion.LOG_NAME_SINGLE_FILE
import it.unibo.jakta.agents.bdi.engine.logging.events.JaktaLogEvent
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.implementation
import it.unibo.jakta.agents.bdi.engine.logging.loggers.appenders.Appenders.buildAppenders
import org.apache.logging.log4j.Logger

class AgentLogger(
    val masID: MasID,
    val agentID: AgentID,
    override val logger: Logger,
) : JaktaLogger {
    override fun log(event: () -> JaktaLogEvent) = logger.implementation(masID, event, agentID)

    companion object {
        fun create(
            masID: MasID,
            agentID: AgentID,
            agentName: String,
            loggingConfig: LoggingConfig,
        ): AgentLogger {
            val name =
                if (loggingConfig.logToSingleFile) {
                    LOG_NAME_SINGLE_FILE
                } else {
                    "$agentName-${agentID.id}"
                }
            val level = loggingConfig.logLevel
            val appenders = buildAppenders(name, loggingConfig)

            LoggerFactory.addLogger(name, level, appenders)
            val logger = JaktaLogger.logger(name)
            return AgentLogger(masID, agentID, logger)
        }
    }
}
