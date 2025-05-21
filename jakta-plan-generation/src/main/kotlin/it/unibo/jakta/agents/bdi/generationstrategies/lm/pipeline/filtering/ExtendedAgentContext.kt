package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering

import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan

data class ExtendedAgentContext(
    val initialGoal: GeneratePlan,
    val context: AgentContext,
    val externalActions: List<ExternalAction>,
)
