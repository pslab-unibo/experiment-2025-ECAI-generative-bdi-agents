package it.unibo.jakta.generationstrategies.lm

import com.aallam.openai.api.chat.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState

data class LMGenerationState(
    override val goal: GeneratePlan,
    override val achievedGoalsHistory: List<Goal> = emptyList(),
    override val consecutiveFailureCount: Int = 0,
    override val logger: KLogger? = null,
    val chatHistory: List<ChatMessage> = emptyList(),
) : GenerationState {

    override fun copy(
        goal: GeneratePlan,
        achievedGoalsHistory: List<Goal>,
        consecutiveFailureCount: Int,
        logger: KLogger?,
    ): LMGenerationState =
        LMGenerationState(
            goal,
            achievedGoalsHistory,
            consecutiveFailureCount,
            logger,
            chatHistory,
        )
}
