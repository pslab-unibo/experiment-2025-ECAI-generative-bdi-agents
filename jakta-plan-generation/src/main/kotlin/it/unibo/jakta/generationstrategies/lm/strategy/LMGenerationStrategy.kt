package it.unibo.jakta.generationstrategies.lm.strategy

import com.aallam.openai.api.chat.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import kotlinx.serialization.json.Json

interface LMGenerationStrategy : GenerationStrategy {
    val promptBuilder: PromptBuilder

    companion object {
        private val json = Json { prettyPrint = true }

        fun KLogger.logChatMessage(msg: ChatMessage) {
            this.info {
                json.encodeToString(
                    ChatMessage.serializer(),
                    msg,
                )
            }
        }

        val configErrorMsg: (GenerationState) -> String = {
            "Expected a LMGenerationState but got ${it.javaClass.simpleName}"
        }
    }
}
