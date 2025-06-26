package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.impl

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import it.unibo.jakta.agents.bdi.engine.logging.loggers.PGPLogger
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestProcessor
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestFailure.NetworkRequestFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestResult
import kotlinx.coroutines.TimeoutCancellationException

internal class RequestProcessorImpl : RequestProcessor {
    override suspend fun requestGeneration(
        api: OpenAI,
        request: ChatCompletionRequest,
        logger: PGPLogger?,
        parser: Parser,
    ): RequestResult {
        return try {
            val completionResponse = api.chatCompletion(request)
            val res =
                completionResponse.choices
                    .firstOrNull()
                    ?.message
                    ?.content
            if (res == null || res.isBlank()) {
                logger?.warn { "API response is empty or invalid" }
                return NetworkRequestFailure("Invalid or empty response from the API")
            }
            parser.parse(res)
        } catch (e: TimeoutCancellationException) {
            logger?.error { "Request timed out: ${e.message}" }
            NetworkRequestFailure("Request timed out")
        } catch (e: Exception) {
            logger?.error { "Error during request generation: ${e.message}" }
            NetworkRequestFailure("Error during request generation: ${e.message}")
        }
    }
}
