package it.unibo.jakta.agents.bdi.logging

import ch.qos.logback.classic.Level
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.NegativeFeedback
import it.unibo.jakta.agents.bdi.logging.events.LogEvent

data class LoggingConfig(
    val logServerURL: String? = null,
    val logToFile: Boolean = false,
    val logToConsole: Boolean = true,
    val logLevel: Level = Level.INFO,
    val logDir: String = "logs",
)

fun KLogger.implementation(event: LogEvent) {
    when {
        event.metadata.isEmpty() -> logSimple(event)
        else -> logWithMetadata(event)
    }
}

private fun KLogger.logSimple(event: LogEvent) {
    if (event is NegativeFeedback) {
        warn { event.description }
    } else {
        info { event.description }
    }
}

private fun KLogger.logWithMetadata(event: LogEvent) {
    if (event is NegativeFeedback) {
        atWarn {
            message = event.description
            payload = event.metadata
        }
    } else {
        atInfo {
            message = event.description
            payload = event.metadata
        }
    }
}
