package it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("PGPFailure")
sealed interface PGPFailure : NegativeFeedback {
    @Serializable
    @SerialName("GenericGenerationFailure")
    data class GenericGenerationFailure(
        override val description: String?,
    ) : PGPFailure
}
