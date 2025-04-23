package it.unibo.jakta.agents.bdi.executionstrategies.feedback

sealed interface GenerationFailure : NegativeFeedback {
    data class GenericGenerationFailure(
        val errorMsg: String,
    ) : GenerationFailure {
        override val description = errorMsg
    }
}
