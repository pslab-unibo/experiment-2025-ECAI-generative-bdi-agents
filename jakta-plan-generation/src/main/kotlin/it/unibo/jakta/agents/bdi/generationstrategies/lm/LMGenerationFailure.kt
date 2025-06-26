package it.unibo.jakta.agents.bdi.generationstrategies.lm

import it.unibo.jakta.agents.bdi.engine.generation.GenerationFailureResult
import it.unibo.jakta.agents.bdi.engine.generation.GenerationState

data class LMGenerationFailure(
    override val generationState: GenerationState,
    override val errorMsg: String,
) : GenerationFailureResult
