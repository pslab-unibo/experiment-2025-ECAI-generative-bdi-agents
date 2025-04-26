package it.unibo.jakta.generationstrategies.lm.pipeline.filtering

import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan

interface ContextFilter {

    fun filter(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
    ): AgentContext

    companion object {
        fun of(): ContextFilter = ContextFilterImpl()
    }
}
