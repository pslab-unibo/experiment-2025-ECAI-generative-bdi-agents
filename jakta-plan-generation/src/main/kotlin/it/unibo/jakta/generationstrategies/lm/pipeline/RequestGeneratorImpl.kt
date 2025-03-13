package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.Failure
import it.unibo.jakta.generationstrategies.lm.GenerationResult
import kotlinx.coroutines.runBlocking

class RequestGeneratorImpl(override val api: OpenAI?) : RequestGenerator {
    override suspend fun requestTextCompletion(logger: KLogger?, request: ChatCompletionRequest): GenerationResult =
        if (api != null) {
            runBlocking {
                val processor = StreamProcessor(api)
                processor.getChatCompletionResult(logger, request)
            }
        } else {
            Failure("No API specified")
        }
}
