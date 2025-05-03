package it.unibo.jakta.generationstrategies.lm

import com.aallam.openai.api.chat.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState

data class LMGenerationState(
    override val goal: GeneratePlan,
    override val logger: KLogger? = null,
    val chatHistory: List<ChatMessage> = emptyList(),
) : GenerationState {

    override fun copy(
        goal: GeneratePlan,
        logger: KLogger?,
    ): LMGenerationState = LMGenerationState(goal, logger, chatHistory)
}
