package it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.engine.logging.events.PlanGenProcedureEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LMGenerationCompleted")
data class LMGenerationCompleted(
    val chatMessage: ChatMessage,
    override val description: String,
) : PlanGenProcedureEvent {
    constructor(chatMessage: ChatMessage) : this(chatMessage, "New chat message")
}
