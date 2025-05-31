package it.unibo.jakta.agents.bdi.engine.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface MessageType

@Serializable
@SerialName("AchievePerformative")
object Achieve : MessageType

@Serializable
@SerialName("TellPerformative")
object Tell : MessageType
