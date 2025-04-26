package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.dsl.LMGenerationConfigScope
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.pipeline.request.StreamProcessor

object GenerationStrategies {
    fun oneStep(generationCfg: LMGenerationConfig) = createStrategy(generationCfg)

    fun oneStep(generationCfg: LMGenerationConfigScope.() -> Unit) =
        createStrategy(LMGenerationConfigScope().also(generationCfg).build())

    private fun createStrategy(lmGenCfg: LMGenerationConfig): LMGenerationStrategyImpl {
        val api = createOpenAIApi(lmGenCfg)
        val streamProcessor = StreamProcessor.of()
        val requestHandler = RequestHandler.of(api, streamProcessor, lmGenCfg)
        val responseParser = Parser.of()
        val planGenerator = LMPlanGenerator.of(requestHandler, responseParser)
        val contextFilter = ContextFilter.of()
        val promptBuilder = PromptBuilder.of(contextFilter, lmGenCfg.remarks, lmGenCfg.withSubgoals)

        return LMGenerationStrategyImpl(planGenerator, promptBuilder)
    }

    private fun createOpenAIApi(lmGenCfg: LMGenerationConfig): OpenAI {
        val host = OpenAIHost(baseUrl = lmGenCfg.lmServerUrl)
        val config = OpenAIConfig(
            host = host,
            token = lmGenCfg.lmServerToken,
            logging = LoggingConfig(logger = Logger.Simple),
            timeout = Timeout(
                request = lmGenCfg.requestTimeout,
                connect = lmGenCfg.connectTimeout,
                socket = lmGenCfg.socketTimeout,
            ),
        )
        return OpenAI(config)
    }
}
