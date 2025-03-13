package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.pipeline.formatter.PromptFormatter

interface PromptGenerator {
    val remarks: List<Remark>
    val promptFormatter: PromptFormatter

    fun buildPrompt(
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        plans: List<Plan>?,
        generatedPlan: GeneratedPlan,
    ): List<ChatMessage>

    companion object {
        fun of(
            remarks: List<Remark> = emptyList(),
            promptFormatter: PromptFormatter = PromptFormatter.of(),
        ) = PromptGeneratorImpl(remarks, promptFormatter)
    }
}
