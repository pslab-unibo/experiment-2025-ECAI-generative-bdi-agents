package it.unibo.jakta.playground.experiment

import ch.qos.logback.classic.Level

enum class LogbackLogLevel(val level: Level) {
    OFF(Level.OFF),
    ERROR(Level.ERROR),
    WARN(Level.WARN),
    INFO(Level.INFO),
    DEBUG(Level.DEBUG),
    TRACE(Level.TRACE),
    ALL(Level.ALL),
}
