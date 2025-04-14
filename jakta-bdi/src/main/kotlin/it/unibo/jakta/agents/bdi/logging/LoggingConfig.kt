package it.unibo.jakta.agents.bdi.logging

import ch.qos.logback.classic.Level
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.logging.events.LogEvent

data class LoggingConfig(
    val logServerURL: String? = null,
    val logToFile: Boolean = false,
    val logToConsole: Boolean = true,
    val logLevel: Level = Level.INFO,
    val logDir: String = "logs",
)

fun KLogger.implementation(event: LogEvent) =
    if (event.metadata.isEmpty()) {
        this.info { event.description } // [${event.name}]
    } else {
        this.atInfo {
            message = event.description // "[${event.name}] ${event.description}"
            payload = event.metadata
        }
    }
