package it.unibo.jakta.agents.bdi.logging

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.slf4j.toKLogger
import it.unibo.jakta.agents.bdi.formatters.serializers.JaktaJsonFactoryDecorator
import net.logstash.logback.encoder.LogstashEncoder
import net.logstash.logback.fieldnames.LogstashFieldNames
import org.slf4j.LoggerFactory

object LoggerFactory {
    private val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext

    private fun createLogstashEncoder(): LogstashEncoder {
        return LogstashEncoder().apply {
            context = loggerContext
            jsonFactoryDecorator = JaktaJsonFactoryDecorator()
            fieldNames = LogstashFieldNames().apply {
                version = null
            }
            start()
        }
    }

    private fun createJsonFileAppender(loggingConfig: LoggingConfig, loggerName: String): FileAppender<ILoggingEvent> =
        FileAppender<ILoggingEvent>().apply {
            context = loggerContext
            name = loggerName
            file = "${loggingConfig.logDir}/$loggerName.jsonl"
            encoder = createLogstashEncoder()
            isAppend = false
            start()
        }

    private fun createLogFileAppender(loggingConfig: LoggingConfig, loggerName: String): FileAppender<ILoggingEvent> =
        FileAppender<ILoggingEvent>().apply {
            context = loggerContext
            name = loggerName
            file = "${loggingConfig.logDir}/$loggerName.log"
            encoder = PatternLayoutEncoder().apply {
                context = loggerContext
                pattern = "%level %logger{36} - %msg%n"
                start()
            }
            isAppend = false
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
                addAppender(createLogFileAppender(loggingConfig, loggerName))
                addAppender(createJsonFileAppender(loggingConfig, loggerName))
            }

            if (loggingConfig.logToConsole) {
                addAppender(createConsoleAppender(loggerName))
            }

            level = loggingConfig.logLevel
            isAdditive = false // Whether to send logs to the root logger
        }.toKLogger()
}
