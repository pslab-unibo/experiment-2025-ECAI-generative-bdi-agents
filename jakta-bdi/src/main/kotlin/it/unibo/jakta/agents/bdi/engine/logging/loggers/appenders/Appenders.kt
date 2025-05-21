package it.unibo.jakta.agents.bdi.engine.logging.loggers.appenders

import it.unibo.jakta.agents.bdi.engine.Jakta.separator
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.CONSOLE_APPENDER_NAME
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.TCP_APPENDER_NAME
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.addFileAppender
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.addTcpAppender
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.jsonLayout
import it.unibo.jakta.agents.bdi.engine.logging.LoggerFactory.logFilePatternLayout
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig

object Appenders {
    fun buildAppenders(
        name: String,
        loggingConfig: LoggingConfig,
    ): List<String> {
        val appenders = mutableListOf<String>()

        if (loggingConfig.logToConsole) {
            appenders.add(CONSOLE_APPENDER_NAME)
        }

        if (loggingConfig.logToFile) {
            val logFileAppenderName = "$name-LogFile"
            addFileAppender(
                logFileAppenderName,
                "${loggingConfig.logDir}$separator$name.log",
                logFilePatternLayout,
            )
            appenders.add(logFileAppenderName)

            val jsonFileAppenderName = "$name-JsonLinesFile"
            addFileAppender(
                jsonFileAppenderName,
                "${loggingConfig.logDir}$separator$name.jsonl",
                jsonLayout,
            )
            appenders.add(jsonFileAppenderName)
        }

        if (loggingConfig.logToServer) {
            addTcpAppender(
                loggingConfig.logServerURL,
                jsonLayout,
            )
            appenders.add(TCP_APPENDER_NAME)
        }

        return appenders
    }
}
