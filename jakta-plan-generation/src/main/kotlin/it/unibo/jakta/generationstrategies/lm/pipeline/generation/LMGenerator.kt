package it.unibo.jakta.generationstrategies.lm.pipeline.generation

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.Generator
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.logChatMessage
import kotlinx.coroutines.runBlocking

interface LMGenerator : Generator {
    val requestHandler: RequestHandler
    val responseParser: ResponseParser

    fun handleParserResults(
        generatingPlan: PartialPlan,
        generationStrategy: LMGenerationStrategy,
        generationResults: ParserResult,
        generationState: LMGenerationState,
    ): GenerationResult

    fun generate(
        generatingPlan: PartialPlan,
        generationStrategy: GenerationStrategy,
        generationState: GenerationState,
    ): GenerationResult = runBlocking {
        val lmGenStrat = generationStrategy as? LMGenerationStrategy
        val lmGenState = generationState as? LMGenerationState

        return@runBlocking when {
            lmGenStrat == null -> LMGenerationFailure(
                generationState,
                "Expected a LMGenerationStrategy but got ${generationStrategy.javaClass.simpleName}",
            )
            lmGenState == null -> LMGenerationFailure(
                generationState,
                "Expected a LMGenerationState but got ${generationState.javaClass.simpleName}",
            )
            else -> {
                val generationResult = requestHandler.requestTextCompletion(generationState, responseParser)

                val chatMessage = ChatMessage(
                    ChatRole.Assistant,
                    generationResult.rawContent,
                )

                val updatedState = generationState.copy(
                    chatHistory = generationState.chatHistory + chatMessage,
                    generationIteration = generationState.generationIteration + 1,
                ).also {
                    generationState.logger?.logChatMessage(chatMessage)
                }

                handleParserResults(
                    generatingPlan,
                    generationStrategy,
                    generationResult,
                    updatedState,
                )
//                .also {
//                    updatedState.chatHistory.forEach { updatedState.logger?.logChatMessage(it) }
//                }
            }
        }
    }

    fun handleParsingFailure(
        errorMsg: String,
        generationState: LMGenerationState,
    ): GenerationResult {
        val newMessage = ChatMessage(ChatRole.User, errorMsg)
        return LMGenerationFailure(
            generationState = generationState.copy(
                chatHistory = generationState.chatHistory + newMessage,
                failedGenerationProcess = generationState.failedGenerationProcess + 1,
            ),
            errorMsg = errorMsg,
        ).also {
            generationState.logger?.logChatMessage(newMessage)
        }
    }
}
