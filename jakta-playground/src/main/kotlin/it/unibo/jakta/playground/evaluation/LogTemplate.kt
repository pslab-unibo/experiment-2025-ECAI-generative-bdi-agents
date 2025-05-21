package it.unibo.jakta.playground.evaluation

import it.unibo.jakta.agents.bdi.engine.logging.events.JaktaLogEventContainer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LogTemplate(
    @SerialName("@timestamp")
    val timestamp: String,
    @SerialName("ecs.version")
    val ecsVersion: String,
    @SerialName("log.level")
    val logLevel: String,
    val message: JaktaLogEventContainer,
    @SerialName("process.thread.name")
    val processThreadName: String,
    @SerialName("log.logger")
    val logLogger: String,
//    val labels: String,
//    val tags: String,
//    @SerialName("error.type")
//    val errorType: String,
//    @SerialName("error.message")
//    val errorMessage: String,
//    @SerialName("error.stack_trace")
//    val errorStackTrace: String
)
