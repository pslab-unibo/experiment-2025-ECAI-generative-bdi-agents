package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request

import com.aallam.openai.client.OpenAI
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.impl.RequestHandlerImpl
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestResult

interface RequestHandler {
    val api: OpenAI?
    val requestProcessor: RequestProcessor
    val generationConfig: LMGenerationConfig.LMGenerationConfigContainer

    suspend fun requestTextCompletion(
        generationState: LMGenerationState,
        parser: Parser,
    ): RequestResult

    companion object {
        fun of(
            generationConfig: LMGenerationConfig.LMGenerationConfigContainer,
            api: OpenAI? = null,
            requestProcessor: RequestProcessor = RequestProcessor.of(),
        ): RequestHandler = RequestHandlerImpl(api, requestProcessor, generationConfig)
    }
}
