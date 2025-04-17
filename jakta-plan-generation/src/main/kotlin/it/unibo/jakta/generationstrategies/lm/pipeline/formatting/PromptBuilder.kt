package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.PromptBuilderImpl

interface PromptBuilder {
    val promptPath: String?
    val remarks: List<Remark>
    val promptFormatter: PromptFormatter

    fun buildPrompt(
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        plans: List<Plan>?,
        goal: String,
    ): List<ChatMessage>

    companion object {
        fun of(
            promptPath: String? = null,
            remarks: List<Remark> = emptyList(),
            promptFormatter: PromptFormatter = PromptFormatter.of(),
        ) = PromptBuilderImpl(promptPath, remarks, promptFormatter)
    }
}
