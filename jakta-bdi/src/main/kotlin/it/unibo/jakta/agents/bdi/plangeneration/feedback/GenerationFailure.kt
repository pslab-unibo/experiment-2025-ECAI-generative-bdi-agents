package it.unibo.jakta.agents.bdi.plangeneration.feedback

sealed interface GenerationFailure : FailureFeedback

data class GenericGenerationFailure(
    val errorMsg: String,
) : GenerationFailure {
    override val description = errorMsg
}
