package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.engine.generation.GenerationResult
import it.unibo.jakta.agents.bdi.engine.generation.GenerationState
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.generation.Generator
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMMessageReceived
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy

interface LMGenerator : Generator {
    val requestHandler: RequestHandler
    val responseParser: Parser

    fun handleRequestResult(
        generationStrategy: LMGenerationStrategy,
        requestResult: RequestResult,
        generationState: LMGenerationState,
    ): GenerationResult

    suspend fun generate(
        generationStrategy: GenerationStrategy,
        generationState: GenerationState,
    ): GenerationResult {
        val lmGenStrat = generationStrategy as? LMGenerationStrategy
        val lmGenState = generationState as? LMGenerationState

        return when {
            lmGenStrat == null ->
                LMGenerationFailure(
                    generationState,
                    "Expected a LMGenerationStrategy but got ${generationStrategy.javaClass.simpleName}",
                )
            lmGenState == null ->
                LMGenerationFailure(
                    generationState,
                    "Expected a LMGenerationState but got ${generationState.javaClass.simpleName}",
                )
            else -> {
                val generationResult = requestHandler.requestTextCompletion(generationState, responseParser)
                val chatMessage = ChatMessage(ChatRole.Assistant, generationResult.rawContent)
                val updatedState =
                    generationState
                        .copy(
                            chatHistory = generationState.chatHistory + chatMessage,
                        ).also {
                            generationState.logger?.log { LMMessageReceived(chatMessage) }
                        }

                handleRequestResult(generationStrategy, generationResult, updatedState)
            }
        }
    }

    fun handleRequestFailure(
        generationResult: RequestFailure,
        generationState: LMGenerationState,
    ): GenerationResult {
        val errorMsg = "Failed parsing"
        val newMessage = ChatMessage(ChatRole.User, errorMsg)
        return LMGenerationFailure(
            generationState =
                generationState.copy(
                    chatHistory = generationState.chatHistory + newMessage,
                ),
            errorMsg = errorMsg,
        ).also {
            generationState.logger?.log { LMMessageReceived(newMessage) }
        }
    }
}
