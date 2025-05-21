package it.unibo.jakta.agents.bdi.engine.plangeneration

interface GenerationFailureResult : GenerationResult {
    val errorMsg: String
}
