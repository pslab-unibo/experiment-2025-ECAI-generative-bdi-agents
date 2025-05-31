package it.unibo.jakta.agents.bdi.engine.logging.loggers

import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.addLogger
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig.Companion.LOG_NAME_SINGLE_FILE
import it.unibo.jakta.agents.bdi.engine.logging.events.JaktaLogEvent
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.implementation
import it.unibo.jakta.agents.bdi.engine.logging.loggers.appenders.Appenders.buildAppenders
import org.apache.logging.log4j.Logger

class MasLogger(
    val masID: MasID,
    override val logger: Logger,
) : JaktaLogger {
    override fun log(event: () -> JaktaLogEvent) = logger.implementation(masID, event)

    companion object {
        fun create(
            masID: MasID,
            loggingConfig: LoggingConfig,
        ): MasLogger {
            val name =
                if (loggingConfig.logToSingleFile) {
                    LOG_NAME_SINGLE_FILE
                } else {
                    "Mas-${masID.id}"
                }
            val level = loggingConfig.logLevel
            val appenders = buildAppenders(name, loggingConfig)

            addLogger(name, level, appenders)
            val logger = JaktaLogger.logger(name)
            return MasLogger(masID, logger)
        }
    }
}
