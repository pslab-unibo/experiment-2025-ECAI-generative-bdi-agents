package it.unibo.jakta.agents.bdi.engine.messages

sealed interface MessageType

object Achieve : MessageType

object Tell : MessageType
