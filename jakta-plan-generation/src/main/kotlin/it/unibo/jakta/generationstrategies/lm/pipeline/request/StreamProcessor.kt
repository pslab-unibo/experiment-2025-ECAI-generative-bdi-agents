package it.unibo.jakta.generationstrategies.lm.pipeline.request

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.request.impl.StreamProcessorImpl

interface StreamProcessor {
    suspend fun requestGeneration(
        api: OpenAI,
        request: ChatCompletionRequest,
        logger: KLogger? = null,
        parser: Parser,
    ): ParserResult

    companion object {
        fun of() = StreamProcessorImpl()
    }
}
