package it.unibo.jakta.generationstrategies.lm.pipeline.request.impl

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.RequestFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.request.StreamProcessor
import kotlin.coroutines.cancellation.CancellationException

class StreamProcessorImpl : StreamProcessor {
    override suspend fun requestGeneration(
        api: OpenAI,
        request: ChatCompletionRequest,
        logger: KLogger?,
        parser: ResponseParser,
    ): ParserResult {
        try {
            val outputBuffer = StringBuilder()
            api.chatCompletions(request)
                .collect { response ->
                    val content = response.choices.first().delta?.content.orEmpty()

                    outputBuffer.append(content)
                    parser.parse(content)

                    if (parser.isComplete()) { return@collect }
                }
            return parser.buildResult()
        } catch (_: CancellationException) {
            logger?.trace { "\nFlow was cancelled." }
            return RequestFailure("Cancelled early")
        } catch (e: Exception) {
            logger?.error { "\nFlow threw an unexpected exception: ${e.message}" }
            return RequestFailure("Error: ${e.message}")
        }
    }
}
