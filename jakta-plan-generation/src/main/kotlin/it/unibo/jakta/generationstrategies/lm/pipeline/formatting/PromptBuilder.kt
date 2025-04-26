package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.PromptBuilderImpl

interface PromptBuilder {
    val contextFilter: ContextFilter
    val remarks: List<Remark>
    val withSubgoals: Boolean

    fun buildPrompt(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction> = emptyList(),
    ): ChatMessage

    companion object {
        fun of(
            contextFilter: ContextFilter = ContextFilter.of(),
            remarks: List<Remark> = emptyList(),
            withSubgoals: Boolean = false,
        ) = PromptBuilderImpl(contextFilter, remarks, withSubgoals)
    }
}
