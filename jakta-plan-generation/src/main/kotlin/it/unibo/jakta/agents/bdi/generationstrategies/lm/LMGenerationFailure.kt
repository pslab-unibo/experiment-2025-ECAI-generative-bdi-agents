package it.unibo.jakta.agents.bdi.generationstrategies.lm

import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationFailureResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationState

data class LMGenerationFailure(
    override val generationState: GenerationState,
    override val errorMsg: String,
) : GenerationFailureResult
