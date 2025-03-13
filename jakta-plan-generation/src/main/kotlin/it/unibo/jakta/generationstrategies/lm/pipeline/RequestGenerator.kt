package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.GenerationResult

interface RequestGenerator {
    val api: OpenAI?

    suspend fun requestTextCompletion(logger: KLogger?, request: ChatCompletionRequest): GenerationResult

    companion object {
        fun of(api: OpenAI? = null): RequestGenerator = RequestGeneratorImpl(api)
    }
}
