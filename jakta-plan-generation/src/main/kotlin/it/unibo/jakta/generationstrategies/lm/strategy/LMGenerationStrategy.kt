package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.OpenAIHost
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig.LMGenerationConfigContainer
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.pipeline.request.StreamProcessor

interface LMGenerationStrategy : GenerationStrategy {
    override val generationConfig: LMGenerationConfig

    companion object {
        fun KLogger.logChatMessage(msg: ChatMessage) {
            atInfo {
                message = "New chat message"
                payload = mapOf(
                    "type" to "chatMessage",
                    "role" to msg.role.role,
                    "content" to msg.content,
                )
            }
        }

        val configErrorMsg: (GenerationState) -> String = {
            "Expected a LMGenerationState but got ${it.javaClass.simpleName}"
        }

        fun of(lmGenCfg: LMGenerationConfigContainer): LMGenerationStrategy {
            val api = createOpenAIApi(lmGenCfg)
            val streamProcessor = StreamProcessor.of()
            val requestHandler = RequestHandler.of(lmGenCfg, api, streamProcessor)
            val responseParser = Parser.of()
            val planGenerator = LMPlanGenerator.of(requestHandler, responseParser)

            return LMGenerationStrategyImpl(planGenerator, lmGenCfg)
        }

        private fun createOpenAIApi(lmGenCfg: LMGenerationConfigContainer): OpenAI {
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
}
