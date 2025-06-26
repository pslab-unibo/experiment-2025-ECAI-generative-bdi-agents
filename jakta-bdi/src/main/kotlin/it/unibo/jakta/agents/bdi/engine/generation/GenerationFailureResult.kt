package it.unibo.jakta.agents.bdi.engine.generation

interface GenerationFailureResult : GenerationResult {
    val errorMsg: String
}
