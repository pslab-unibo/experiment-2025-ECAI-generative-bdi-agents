package it.unibo.jakta.agents.bdi.engine.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.events.EventQueue
import it.unibo.jakta.agents.bdi.engine.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.engine.intentions.SchedulingResult
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import java.util.UUID

internal data class AgentImpl(
    override val masID: MasID?,
    override val context: AgentContext,
    override val agentID: AgentID = AgentID(),
    override val name: String = "Agent-" + UUID.randomUUID(),
    override val generationStrategy: GenerationStrategy? = null,
    override val loggingConfig: LoggingConfig? = null,
    override val logger: AgentLogger? = null,
    override val tags: Map<String, Any> = emptyMap(),
) : Agent {
    override fun selectEvent(events: EventQueue) = events.firstOrNull()

    override fun selectApplicablePlan(plans: Iterable<Plan>) = plans.firstOrNull()

    override fun scheduleIntention(intentions: IntentionPool) =
        SchedulingResult(intentions.pop(), intentions.nextIntention())

    override fun replaceTags(tags: Map<String, Any>): Agent =
        if (tags != this.tags) {
            copy(tags = tags)
        } else {
            this
        }
}
