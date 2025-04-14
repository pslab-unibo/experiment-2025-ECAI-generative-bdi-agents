package it.unibo.jakta.agents.bdi.context

import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.actions.InternalActions
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.impl.AgentContextImpl
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.EventQueue
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.plangeneration.pool.GenerationRequestPool
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.nlp.literateprolog.LiteratePrologTemplate

/**
 * The Context is the actual state of a BDI Agent's structures.
 */
interface AgentContext {

    /** [BeliefBase] of the BDI Agent */
    val beliefBase: BeliefBase

    /** [Event]s on which the BDI Agent reacts */
    val events: EventQueue

    /** [Plan]s collection of the BDI Agent */
    val planLibrary: PlanLibrary

    val internalActions: Map<String, InternalAction>

    val generationRequests: GenerationRequestPool

    val intentions: IntentionPool

    val templates: List<LiteratePrologTemplate>

    fun copy(
        beliefBase: BeliefBase = this.beliefBase,
        events: EventQueue = this.events,
        planLibrary: PlanLibrary = this.planLibrary,
        internalActions: Map<String, InternalAction> = this.internalActions,
        generationRequests: GenerationRequestPool = this.generationRequests,
        intentions: IntentionPool = this.intentions,
        templates: List<LiteratePrologTemplate> = this.templates,
    ): AgentContext = AgentContextImpl(
        beliefBase,
        events,
        planLibrary,
        internalActions,
        generationRequests,
        intentions,
        templates,
    )

    companion object {
        fun of(
            beliefBase: BeliefBase = BeliefBase.empty(),
            events: EventQueue = emptyList(),
            planLibrary: PlanLibrary = PlanLibrary.empty(),
            internalActions: Map<String, InternalAction> = InternalActions.default(),
            generationRequests: GenerationRequestPool = GenerationRequestPool.empty(),
            intentions: IntentionPool = IntentionPool.empty(),
            templates: List<LiteratePrologTemplate> = emptyList(),
        ): AgentContext = AgentContextImpl(
            beliefBase,
            events,
            planLibrary,
            internalActions,
            generationRequests,
            intentions,
            templates,
        )
    }
}
