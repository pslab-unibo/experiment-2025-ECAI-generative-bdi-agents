package it.unibo.jakta.playground.evaluation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LegacyLogEntry(
    @SerialName("@timestamp")
    val timestamp: String,
    val message: String,
    @SerialName("logger_name")
    val loggerName: String,
    @SerialName("thread_name")
    val threadName: String,
    val level: String,
    @SerialName("level_value")
    val levelValue: Int,
    val type: String,
    val role: String,
    val content: String,
)
