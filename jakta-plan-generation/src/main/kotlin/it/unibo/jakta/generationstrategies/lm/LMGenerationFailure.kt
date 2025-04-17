package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.plangeneration.FailureResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState

data class LMGenerationFailure(
    override val generationState: GenerationState,
    override val errorMsg: String,
) : FailureResult {
    operator fun plus(other: LMGenerationFailure): LMGenerationFailure {
        return LMGenerationFailure(
            generationState,
            errorMsg + "\n" + other.errorMsg,
        )
    }
}
