package it.unibo.jakta.generationstrategies.lm

import com.aallam.openai.api.chat.ChatMessage
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.plans.generation.GenerationState

class LMGenerationState(
    var history: List<ChatMessage> = emptyList(),
    val startedGeneration: Boolean = false,
    val finishedGeneration: Boolean = false,
    override val externalActions: List<ExternalAction> = emptyList(),
    override val logger: KLogger? = null,
    override val context: AgentContext? = null,
) : GenerationState
