package it.unibo.jakta.agents.bdi.plangeneration

interface GenerationFailureResult : GenerationResult {
    val errorMsg: String
}
