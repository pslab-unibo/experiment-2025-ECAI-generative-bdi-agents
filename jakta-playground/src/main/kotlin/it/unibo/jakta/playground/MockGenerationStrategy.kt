package it.unibo.jakta.playground

import com.aallam.openai.api.chat.ChatChoice
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import io.mockk.coEvery
import io.mockk.mockk
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy

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
                choices =
                    listOf(
                        ChatChoice(
                            index = 0,
                            message =
                                ChatMessage(
                                    role = ChatRole.Assistant,
                                    content = response,
                                ),
                        ),
                    ),
                created = System.currentTimeMillis(),
                model = ModelId("mock-model"),
            )
        }

        val lmConfig = LMGenerationConfig.LMGenerationConfigContainer()
        val requestHandler = RequestHandler.of(generationConfig = lmConfig, api = api)
        val responseParser = Parser.of()
        val planGenerator = LMPlanGenerator.of(requestHandler, responseParser)
        return LMGenerationStrategy.of(planGenerator, lmConfig)
    }
}
