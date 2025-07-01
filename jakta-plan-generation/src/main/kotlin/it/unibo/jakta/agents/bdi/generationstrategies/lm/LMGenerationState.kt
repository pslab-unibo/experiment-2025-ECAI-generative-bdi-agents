package it.unibo.jakta.agents.bdi.generationstrategies.lm

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.engine.generation.GenerationState
import it.unibo.jakta.agents.bdi.engine.generation.PgpID
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.loggers.PGPLogger

data class LMGenerationState(
    override val pgpID: PgpID,
    override val goal: GeneratePlan,
    override val logger: PGPLogger? = null,
    val chatHistory: List<ChatMessage> = emptyList(),
) : GenerationState {
    override fun copy(
        pgpID: PgpID,
        goal: GeneratePlan,
        logger: PGPLogger?,
    ): LMGenerationState = LMGenerationState(pgpID, goal, logger, chatHistory)
}
