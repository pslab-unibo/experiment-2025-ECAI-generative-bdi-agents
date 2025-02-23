package it.unibo.jakta.agents.bdi.plans.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext

interface GenerationState {
    val externalActions: List<ExternalAction>
    val logger: KLogger?
    val context: AgentContext?
}
