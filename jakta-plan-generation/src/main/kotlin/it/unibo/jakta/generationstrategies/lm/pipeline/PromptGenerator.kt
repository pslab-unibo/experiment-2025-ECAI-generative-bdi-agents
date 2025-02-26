package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.generationstrategies.lm.Remark

interface PromptGenerator {
    val remarks: List<Remark>

    fun buildPrompt(
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        planLibrary: PlanLibrary?,
        generatedPlan: GeneratedPlan,
    ): List<ChatMessage>
}
