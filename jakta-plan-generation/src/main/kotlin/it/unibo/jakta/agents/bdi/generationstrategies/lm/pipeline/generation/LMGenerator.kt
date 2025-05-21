package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.Generator
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMGenerationCompleted
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.GenericParserFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy

interface LMGenerator : Generator {
    val requestHandler: RequestHandler
    val responseParser: Parser

    fun handleParserResults(
        generationStrategy: LMGenerationStrategy,
        generationResult: ParserResult,
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
                            generationState.logger?.log { LMGenerationCompleted(chatMessage) }
                        }

                handleParserResults(generationStrategy, generationResult, updatedState)
            }
        }
    }

    fun handleParsingFailure(
        generationResult: GenericParserFailure,
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
            generationState.logger?.log { LMGenerationCompleted(newMessage) }
        }
    }
}
