package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request

import com.aallam.openai.client.OpenAI
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.impl.RequestHandlerImpl

interface RequestHandler {
    val api: OpenAI?
    val streamProcessor: StreamProcessor
    val generationConfig: LMGenerationConfig.LMGenerationConfigContainer

    suspend fun requestTextCompletion(
        generationState: LMGenerationState,
        parser: Parser,
    ): ParserResult

    companion object {
        fun of(
            generationConfig: LMGenerationConfig.LMGenerationConfigContainer,
            api: OpenAI? = null,
            streamProcessor: StreamProcessor = StreamProcessor.of(),
        ): RequestHandler = RequestHandlerImpl(api, streamProcessor, generationConfig)
    }
}
