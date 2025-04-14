package it.unibo.jakta.agents.bdi.plangeneration

interface FailureResult : GenerationResult {
    val generationState: GenerationState
    val errorMsg: String
}
