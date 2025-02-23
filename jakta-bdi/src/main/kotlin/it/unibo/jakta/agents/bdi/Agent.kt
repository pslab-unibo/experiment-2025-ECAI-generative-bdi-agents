package it.unibo.jakta.agents.bdi

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.actions.InternalActions
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.EventQueue
import it.unibo.jakta.agents.bdi.impl.AgentImpl
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.intentions.SchedulingResult
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import it.unibo.tuprolog.utils.Taggable
import java.util.*

interface Agent : Taggable<Agent> {

    val agentID: AgentID

    val name: String

    val logger: KLogger?

    val generationStrategy: GenerationStrategy?

    /** Snapshot of Agent's Actual State */
    val context: AgentContext

    /** Event Selection Function*/
    fun selectEvent(events: EventQueue): Event?

    /** Plan Selection Function */
    fun selectApplicablePlan(plans: Iterable<Plan>): Plan?

    /** Intention Selection Function */
    fun scheduleIntention(intentions: IntentionPool): SchedulingResult

    fun copy(agentContext: AgentContext = this.context) =
        of(this.agentID, this.name, this.logger, this.generationStrategy, agentContext.copy())

    fun copy(
        logger: KLogger? = this.logger,
        generationStrategy: GenerationStrategy? = this.generationStrategy,
        beliefBase: BeliefBase = this.context.beliefBase,
        events: EventQueue = this.context.events,
        planLibrary: PlanLibrary = this.context.planLibrary,
        intentions: IntentionPool = this.context.intentions,
        internalActions: Map<String, InternalAction> = this.context.internalActions,
    ) = of(
        this.agentID,
        this.name,
        logger,
        generationStrategy,
        context.copy(beliefBase, events, planLibrary, intentions, internalActions),
    )

    companion object {
        fun empty(): Agent = AgentImpl(AgentContext.of())
        fun of(
            agentID: AgentID = AgentID(),
            name: String = "Agent-" + UUID.randomUUID(),
            logger: KLogger? = null,
            generationStrategy: GenerationStrategy? = null,
            beliefBase: BeliefBase = BeliefBase.empty(),
            events: EventQueue = emptyList(),
            planLibrary: PlanLibrary = PlanLibrary.empty(),
            internalActions: Map<String, InternalAction> = InternalActions.default(),
        ): Agent = AgentImpl(
            AgentContext.of(beliefBase, events, planLibrary, internalActions),
            agentID,
            name,
            logger,
            generationStrategy,
        )

        fun of(
            agentID: AgentID = AgentID(),
            name: String = "Agent-" + UUID.randomUUID(),
            logger: KLogger? = null,
            generationStrategy: GenerationStrategy? = null,
            agentContext: AgentContext,
        ): Agent = AgentImpl(agentContext, agentID, name, logger, generationStrategy)
    }
}
