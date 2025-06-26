package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import it.unibo.jakta.agents.bdi.engine.logging.loggers.PGPLogger
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.impl.RequestProcessorImpl
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestResult

interface RequestProcessor {
    suspend fun requestGeneration(
        api: OpenAI,
        request: ChatCompletionRequest,
        logger: PGPLogger? = null,
        parser: Parser,
    ): RequestResult

    companion object {
        fun of(): RequestProcessor = RequestProcessorImpl()
    }
}
