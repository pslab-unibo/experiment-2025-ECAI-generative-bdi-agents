package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.GenerationResult

interface RequestGenerator {
    suspend fun requestTextCompletion(logger: KLogger?, request: ChatCompletionRequest): GenerationResult
}
