package it.unibo.jakta.agents.bdi.dsl.logging

import ch.qos.logback.classic.Level
import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.logging.LoggingConfig

class LoggingConfigScope : Builder<LoggingConfig> {
    var logServerURL: String? = null
    var logToFile: Boolean = false
    var logToConsole: Boolean = true
    var logLevel: Level = Level.INFO
    var logDir: String = "logs"

    fun debug() {
        logLevel = Level.DEBUG
    }

    fun info() {
        logLevel = Level.INFO
    }

    fun warn() {
        logLevel = Level.WARN
    }

    fun error() {
        logLevel = Level.ERROR
    }

    /**
     * Enables file logging with an optional custom directory.
     *
     * @param directory The directory where log files will be stored. The default is "logs".
     */
    fun enableFileLogging(directory: String = "logs") {
        logToFile = true
        logDir = directory
    }

    fun disableConsoleLogging() {
        logToConsole = false
    }

    /**
     * Sets up remote logging to a server.
     *
     * @param url The URL of the logging server.
     */
    fun remoteLogging(url: String) {
        logServerURL = url
    }

    override fun build(): LoggingConfig =
        LoggingConfig(
            logServerURL = logServerURL,
            logToFile = logToFile,
            logToConsole = logToConsole,
            logLevel = logLevel,
            logDir = logDir,
        )
}
