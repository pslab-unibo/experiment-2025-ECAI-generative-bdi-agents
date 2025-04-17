package it.unibo.jakta.generationstrategies.lm.pipeline.request.impl

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.configuration.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.RequestFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.pipeline.request.StreamProcessor

class RequestHandlerImpl(
    override val api: OpenAI?,
    override val streamProcessor: StreamProcessor,
    override val generationConfig: LMGenerationConfig,
) : RequestHandler {
    override suspend fun requestTextCompletion(
        generationState: LMGenerationState,
        parser: ResponseParser,
    ): ParserResult = if (api != null) {
        val request = makeTextCompletionRequest(generationConfig, generationState)
        streamProcessor.requestGeneration(api, request, generationState.logger, parser)
    } else {
        RequestFailure("No API specified")
    }

    private fun makeTextCompletionRequest(
        cfg: LMGenerationConfig,
        state: LMGenerationState,
    ): ChatCompletionRequest =
        ChatCompletionRequest(
            model = ModelId(cfg.modelId),
            temperature = cfg.temperature,
            messages = state.chatHistory,
            maxTokens = cfg.maxTokens,
        )
}
