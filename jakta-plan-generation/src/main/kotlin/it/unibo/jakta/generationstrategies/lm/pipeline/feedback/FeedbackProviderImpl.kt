package it.unibo.jakta.generationstrategies.lm.pipeline.feedback

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ExecutionFeedback
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.FeedbackFormatter
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.configErrorMsg
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.logChatMessage

class FeedbackProviderImpl(
    override val feedbackFormatter: FeedbackFormatter,
) : FeedbackProvider {
    override fun provideGenerationFeedback(
        generationState: GenerationState,
        executionFeedback: ExecutionFeedback,
    ): GenerationState {
        return when (val lmState = generationState as? LMGenerationState) {
            null -> {
                generationState.logger?.error { configErrorMsg(generationState) }
                generationState
            }
            else -> updateGenerationState(lmState, executionFeedback)
        }
    }

    private fun updateGenerationState(
        lmState: LMGenerationState,
        executionFeedback: ExecutionFeedback,
    ): GenerationState {
        val chatMessageContent = feedbackFormatter.format(executionFeedback)
        if (chatMessageContent.isBlank()) return lmState

        val chatMessage = ChatMessage(ChatRole.User, chatMessageContent)
        val updatedState = lmState.addChatMessage(chatMessage)

        return updatedState.also {
            it.logger?.logChatMessage(chatMessage)
        }
//        when {
//            executionFeedback is FailureFeedback -> updatedState.addFailedMessage(chatMessage)
//            updatedState.failedMessagesHistory.isNotEmpty() -> updatedState.removeFailedMessages()
//            else -> updatedState
//        }
    }
}
