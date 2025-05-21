package it.unibo.jakta.playground.experiment

import org.apache.logging.log4j.Level

enum class Log4jLevel(
    val level: Level,
) {
    OFF(Level.OFF),
    FATAL(Level.FATAL),
    ERROR(Level.ERROR),
    WARN(Level.WARN),
    INFO(Level.INFO),
    DEBUG(Level.DEBUG),
    TRACE(Level.TRACE),
    ALL(Level.ALL),
}
