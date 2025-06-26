package it.unibo.jakta.agents.bdi.generationstrategies.lm

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.engine.generation.GenerationState
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.loggers.PGPLogger

data class LMGenerationState(
    override val goal: GeneratePlan,
    override val logger: PGPLogger? = null,
    val chatHistory: List<ChatMessage> = emptyList(),
) : GenerationState {
    override fun copy(
        goal: GeneratePlan,
        logger: PGPLogger?,
    ): LMGenerationState = LMGenerationState(goal, logger, chatHistory)
}
