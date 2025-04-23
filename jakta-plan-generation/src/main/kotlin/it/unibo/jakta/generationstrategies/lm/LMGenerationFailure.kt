package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.plangeneration.GenerationFailureResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState

data class LMGenerationFailure(
    override val generationState: GenerationState,
    override val errorMsg: String,
) : GenerationFailureResult
