package it.unibo.jakta.generationstrategies.lm

import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult

data class LMBinaryResult(val affirmativeResponse: Boolean) : GenerationResult
