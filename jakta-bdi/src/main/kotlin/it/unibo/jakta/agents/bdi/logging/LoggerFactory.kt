package it.unibo.jakta.agents.bdi.logging

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.JsonEncoder
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.slf4j.toKLogger
import org.slf4j.LoggerFactory

object LoggerFactory {
    private val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

    private fun createFileAppender(loggingConfig: LoggingConfig, loggerName: String): FileAppender<ILoggingEvent> =
        FileAppender<ILoggingEvent>().apply {
            context = loggerContext
            name = loggerName
            file = "${loggingConfig.logDir}/$loggerName.json"
            encoder = JsonEncoder()
            start()
        }

    private fun createConsoleAppender(loggerName: String): ConsoleAppender<ILoggingEvent> =
        ConsoleAppender<ILoggingEvent>().apply {
            context = loggerContext
            name = loggerName
            encoder = PatternLayoutEncoder().apply {
                context = loggerContext
                pattern = "%highlight(%level) %yellow(%logger{36}) - %msg%n" // %kvp%n
                start()
            }
            start()
        }

    fun createLogger(loggingConfig: LoggingConfig, loggerName: String): KLogger =
        loggerContext.getLogger(loggerName).apply {
            if (loggingConfig.logToFile) {
                val fileAppender = createFileAppender(loggingConfig, loggerName)
                addAppender(fileAppender)
            }

            if (loggingConfig.logToConsole) {
                val stdOutAppender = createConsoleAppender(loggerName)
                addAppender(stdOutAppender)
            }

            level = loggingConfig.logLevel
            isAdditive = false // Whether to send it to the root logger
        }.toKLogger()
}
