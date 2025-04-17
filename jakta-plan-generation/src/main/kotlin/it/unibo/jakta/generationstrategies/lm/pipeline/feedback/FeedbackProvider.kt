package it.unibo.jakta.generationstrategies.lm.pipeline.feedback

import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ExecutionFeedback
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.FeedbackFormatter

interface FeedbackProvider {
    val feedbackFormatter: FeedbackFormatter

    fun provideGenerationFeedback(
        generationState: GenerationState,
        executionFeedback: ExecutionFeedback,
    ): GenerationState

    companion object {
        fun of(feedbackFormatter: FeedbackFormatter): FeedbackProvider =
            FeedbackProviderImpl(feedbackFormatter)
    }
}
