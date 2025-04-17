package it.unibo.jakta.generationstrategies.lm.pipeline.termination

import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMBinaryAnswerGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.impl.BinaryQuestionHandlerImpl
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy

interface BinaryQuestionHandler {
    val answerGenerator: LMBinaryAnswerGenerator

    fun setupClarification(generationState: LMGenerationState): LMGenerationState

    fun askPossibility(
        generatedPlan: PartialPlan,
        generationState: LMGenerationState,
        generationStrategy: LMGenerationStrategy,
    ): LMGenerationState

    fun askConfirmation(
        generatedPlan: PartialPlan,
        generationState: LMGenerationState,
        generationStrategy: LMGenerationStrategy,
    ): LMGenerationState

    companion object {
        fun of(planGenerator: LMBinaryAnswerGenerator) = BinaryQuestionHandlerImpl(planGenerator)
    }
}
