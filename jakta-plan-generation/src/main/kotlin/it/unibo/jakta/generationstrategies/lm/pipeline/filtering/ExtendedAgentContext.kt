package it.unibo.jakta.generationstrategies.lm.pipeline.filtering

import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan

data class ExtendedAgentContext(
    val initialGoal: GeneratePlan,
    val context: AgentContext,
    val externalActions: List<ExternalAction>,
)
