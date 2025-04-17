package it.unibo.jakta.generationstrategies.lm.pipeline.request

import com.aallam.openai.client.OpenAI
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.configuration.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.request.impl.RequestHandlerImpl

interface RequestHandler {
    val api: OpenAI?
    val streamProcessor: StreamProcessor
    val generationConfig: LMGenerationConfig

    suspend fun requestTextCompletion(
        generationState: LMGenerationState,
        parser: ResponseParser,
    ): ParserResult

    companion object {
        fun of(
            api: OpenAI? = null,
            streamProcessor: StreamProcessor = StreamProcessor.of(),
            generationConfig: LMGenerationConfig = LMGenerationConfig(),
        ): RequestHandler = RequestHandlerImpl(api, streamProcessor, generationConfig)
    }
}
