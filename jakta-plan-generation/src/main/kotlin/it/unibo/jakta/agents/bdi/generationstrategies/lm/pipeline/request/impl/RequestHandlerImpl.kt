package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.impl

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig.LMGenerationConfigContainer
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMGenerationRequested
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.NetworkRequestFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.StreamProcessor

internal class RequestHandlerImpl(
    override val api: OpenAI?,
    override val streamProcessor: StreamProcessor,
    override val generationConfig: LMGenerationConfigContainer,
) : RequestHandler {
    override suspend fun requestTextCompletion(
        generationState: LMGenerationState,
        parser: Parser,
    ): ParserResult =
        if (api != null) {
            val request = makeTextCompletionRequest(generationConfig, generationState)
            streamProcessor.requestGeneration(api, request, generationState.logger, parser).also {
                generationState.logger?.log { LMGenerationRequested(generationConfig) }
            }
        } else {
            NetworkRequestFailure("No API specified")
        }

    private fun makeTextCompletionRequest(
        cfg: LMGenerationConfigContainer,
        state: LMGenerationState,
    ): ChatCompletionRequest =
        ChatCompletionRequest(
            model = ModelId(cfg.modelId),
            temperature = cfg.temperature,
            messages = state.chatHistory,
            maxTokens = cfg.maxTokens,
        )
}
