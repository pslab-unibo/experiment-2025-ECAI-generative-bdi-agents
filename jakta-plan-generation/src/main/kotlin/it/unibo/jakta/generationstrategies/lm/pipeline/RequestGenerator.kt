package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.FinishResult

interface RequestGenerator {
    suspend fun requestTextCompletion(logger: KLogger?, request: ChatCompletionRequest): FinishResult
}
