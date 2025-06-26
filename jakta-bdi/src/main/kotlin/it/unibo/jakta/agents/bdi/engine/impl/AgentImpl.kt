package it.unibo.jakta.agents.bdi.engine.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.events.EventQueue
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.engine.intentions.SchedulingResult
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("Agent")
internal data class AgentImpl(
    override val masID: MasID?,
    @Transient
    override val context: AgentContext = AgentContext.of(),
    override val agentID: AgentID = AgentID(),
    @Transient
    override val generationStrategy: GenerationStrategy? = null,
    @Transient
    override val loggingConfig: LoggingConfig? = null,
    @Transient
    override val logger: AgentLogger? = null,
    @Transient
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
