package it.unibo.jakta.agents.bdi.plans.generation

import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext

interface GenerationState {
    val externalActions: List<ExternalAction>
    val context: AgentContext?
}
