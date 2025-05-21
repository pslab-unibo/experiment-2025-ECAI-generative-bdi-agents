package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PositiveFeedback")
sealed interface PositiveFeedback : ExecutionFeedback
