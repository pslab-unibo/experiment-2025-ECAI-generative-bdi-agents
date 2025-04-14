package it.unibo.jakta.agents.bdi.context

import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.actions.InternalActions
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.impl.AgentContextImpl
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.EventQueue
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.jakta.agents.bdi.plangeneration.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

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

    val generationProcesses: GenerationProcessRegistry

    val intentions: IntentionPool

    val templates: List<LiteratePrologTemplate>

    fun copy(
        beliefBase: BeliefBase = this.beliefBase,
        events: EventQueue = this.events,
        planLibrary: PlanLibrary = this.planLibrary,
        internalActions: Map<String, InternalAction> = this.internalActions,
        generationProcess: GenerationProcessRegistry = this.generationProcesses,
        intentions: IntentionPool = this.intentions,
        templates: List<LiteratePrologTemplate> = this.templates,
    ): AgentContext = AgentContextImpl(
        beliefBase,
        events,
        planLibrary,
        internalActions,
        generationProcess,
        intentions,
        templates,
    )

    companion object {
        fun of(
            beliefBase: BeliefBase = BeliefBase.empty(),
            events: EventQueue = emptyList(),
            planLibrary: PlanLibrary = PlanLibrary.empty(),
            internalActions: Map<String, InternalAction> = InternalActions.default(),
            generationProcess: GenerationProcessRegistry = GenerationProcessRegistry.empty(),
            intentions: IntentionPool = IntentionPool.empty(),
            templates: List<LiteratePrologTemplate> = emptyList(),
        ): AgentContext = AgentContextImpl(
            beliefBase,
            events,
            planLibrary,
            internalActions,
            generationProcess,
            intentions,
            templates,
        )
    }
}
