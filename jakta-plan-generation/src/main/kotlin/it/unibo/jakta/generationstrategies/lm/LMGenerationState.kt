package it.unibo.jakta.generationstrategies.lm

import com.aallam.openai.api.chat.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plans.PlanID

data class LMGenerationState(
    override val goal: Generate,
    override val rootPlanID: PlanID,
    override val achievedGoalsHistory: List<Goal> = emptyList(),
    override val logger: KLogger? = null,
    override val generationIteration: Int = 0,
    override val isGenerationFinished: Boolean = false,
    override val isGenerationEndConfirmed: Boolean = false,
    override val failedGenerationProcess: Int = 0,
    val chatHistory: List<ChatMessage> = emptyList(),
//    val failedMessagesHistory: List<ChatMessage> = emptyList(),
) : GenerationState {

    override fun reset(): LMGenerationState =
        LMGenerationState(
            goal = goal,
            rootPlanID = rootPlanID,
            logger = logger,
            chatHistory = chatHistory, // .subList(0, 2), // only keep system and user prompt
        )

    override fun copy(
        goal: Generate,
        achievedGoalsHistory: List<Goal>,
        rootPlanID: PlanID,
        logger: KLogger?,
        isGenerationFinished: Boolean,
        isGenerationEndConfirmed: Boolean,
        generationIteration: Int,
        failedGenerationProcess: Int,
    ): LMGenerationState =
        copy(
            goal,
            rootPlanID,
            achievedGoalsHistory,
            logger,
            generationIteration,
            isGenerationFinished,
            isGenerationEndConfirmed,
            failedGenerationProcess,
            chatHistory,
//            failedMessagesHistory,
        )

    fun addChatMessage(message: ChatMessage): LMGenerationState =
        copy(chatHistory = chatHistory + message)

//    fun addFailedMessage(message: ChatMessage): LMGenerationState =
//        copy(failedMessagesHistory = failedMessagesHistory + message)
//
//    fun removeFailedMessages(): LMGenerationState =
//        copy(
//            chatHistory = chatHistory - failedMessagesHistory,
//            failedMessagesHistory = emptyList(),
//        )
}
