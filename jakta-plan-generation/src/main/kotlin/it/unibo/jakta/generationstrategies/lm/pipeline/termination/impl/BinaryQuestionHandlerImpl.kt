package it.unibo.jakta.generationstrategies.lm.pipeline.termination.impl

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.generationstrategies.lm.LMBinaryResult
import it.unibo.jakta.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMBinaryAnswerGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.BinaryQuestionHandler
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.impl.BinaryQuestionHandlerImpl.BinaryQuestion.Confirmation
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.impl.BinaryQuestionHandlerImpl.BinaryQuestion.Possibility
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.impl.BinaryQuestionHandlerImpl.Utterance.Clarification
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.configErrorMsg
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.logChatMessage

class BinaryQuestionHandlerImpl(
    override val answerGenerator: LMBinaryAnswerGenerator,
) : BinaryQuestionHandler {

    private sealed interface Utterance {
        object Clarification : Utterance
    }

    private sealed interface BinaryQuestion : Utterance {
        object Possibility : BinaryQuestion
        object Confirmation : BinaryQuestion
    }

    override fun setupClarification(generationState: LMGenerationState) =
        addUtterance(Clarification, generationState)

    override fun askPossibility(
        generatedPlan: PartialPlan,
        generationState: LMGenerationState,
        generationStrategy: LMGenerationStrategy,
    ): LMGenerationState {
        val updatedState = addUtterance(Possibility, generationState)
        return askQuestion(Possibility, updatedState, generatedPlan, generationStrategy)
    }

    override fun askConfirmation(
        generatedPlan: PartialPlan,
        generationState: LMGenerationState,
        generationStrategy: LMGenerationStrategy,
    ): LMGenerationState {
        val updatedState = addUtterance(Confirmation, generationState)
        return askQuestion(Confirmation, updatedState, generatedPlan, generationStrategy)
    }

    private fun buildUtterance(questionType: Utterance): String =
        StringBuilder().apply {
            when (questionType) {
                is Clarification -> {
                    appendLine("Since the goal is not completed yet, suggest the next step.")
                    appendLine("Remember to follow the format indicated before.")
                }
                is BinaryQuestion -> {
                    val question = when (questionType) {
                        is Possibility ->
                            "Is it possible to complete the provided goal successfully given the current state?"
                        is Confirmation -> "Did the provided goal complete successfully?"
                    }
                    appendLine(question)
                    appendLine("Answer strictly with 'yes' or 'no' only, then provide a short explanation.")
                    appendLine()
                    appendLine("Answer:")
                }
            }
        }.toString()

    private fun addUtterance(
        utteranceType: Utterance,
        generationState: LMGenerationState,
    ): LMGenerationState {
        val msgContent = buildUtterance(utteranceType)
        val msg = ChatMessage(ChatRole.User, msgContent)

        return generationState.copy(
            isGenerationFinished = false,
            chatHistory = generationState.chatHistory + msg,
        ).also {
            it.logger?.logChatMessage(msg)
        }
    }

    private fun askQuestion(
        binaryQuestion: BinaryQuestion,
        generationState: LMGenerationState,
        generatedPlan: PartialPlan,
        generationStrategy: LMGenerationStrategy,
    ): LMGenerationState {
        val result = answerGenerator.generate(
            generatedPlan,
            generationStrategy,
            generationState,
        )
        val res = when (result) {
            is LMBinaryResult -> when (binaryQuestion) {
                is Possibility -> generationState.copy(
                    isGenerationFinished = true,
                    isGenerationEndConfirmed = !result.affirmativeResponse,
                )
                is Confirmation -> generationState.copy(
                    isGenerationFinished = true,
                    isGenerationEndConfirmed = result.affirmativeResponse,
                )
            }
            is LMGenerationFailure -> generationState.also {
                generationState.logger?.error { "Failed generation due to: ${result.errorMsg}" }
            }
            else -> generationState.also {
                generationState.logger?.error { "Failed generation due to an unknown result" }
            }
        }
        @Suppress("USELESS_CAST")
        return (res as? LMGenerationState) ?: generationState.also {
            generationState.logger?.error { configErrorMsg(generationState) }
        }
    }
}
