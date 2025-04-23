package it.unibo.jakta.playground

import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import it.unibo.jakta.agents.bdi.Jakta.toLeftNestedAnd
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategyImpl
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import kotlinx.coroutines.runBlocking

object MockGenerationStrategy {
    fun createOneStepStrategyWithMockedAPI(trace: List<String>): LMGenerationStrategy {
        val api = mockk<OpenAI>()
        var callCount = 0

        coEvery {
            api.chatCompletion(any<ChatCompletionRequest>())
        } answers {
            val index = callCount.coerceAtMost(trace.size - 1)
            val response = trace[index]
            callCount++

            ChatCompletion(
                id = "mock-completion-id",
                choices = listOf(
                    ChatChoice(
                        index = 0,
                        message = ChatMessage(
                            role = ChatRole.Assistant,
                            content = response,
                        ),
                    ),
                ),
                created = System.currentTimeMillis(),
                model = ModelId("mock-model"),
            )
        }

        val requestHandler = RequestHandler.Companion.of(api = api)
        val responseParser = Parser.Companion.of()
        val planGenerator = LMPlanGenerator.Companion.of(requestHandler, responseParser)
        return LMGenerationStrategyImpl(planGenerator, PromptBuilder.Companion.of())
    }

    fun createOneStepStrategyWithMockedParser(trace: List<ParserResult>): LMGenerationStrategy {
        val requestHandler = mockk<RequestHandler>()
        var callCount = 0
        every {
            runBlocking { requestHandler.requestTextCompletion(any(), any()) }
        } answers {
            val index = if (trace.size <= callCount) {
                trace.size - 1
            } else {
                callCount.coerceAtMost(trace.size - 1)
            }
            callCount++
            trace[index]
        }

        val responseParser = Parser.Companion.of()
        val planGenerator = LMPlanGenerator.Companion.of(requestHandler, responseParser)
        return LMGenerationStrategyImpl(planGenerator, PromptBuilder.Companion.of())
    }

    fun toGuard(vararg struct: Struct) =
        struct.toList().toLeftNestedAnd()?.castToStruct() ?: Truth.Companion.TRUE
}
