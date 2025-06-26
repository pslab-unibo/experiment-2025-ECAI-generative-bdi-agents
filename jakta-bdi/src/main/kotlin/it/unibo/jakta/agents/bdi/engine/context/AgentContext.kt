package it.unibo.jakta.agents.bdi.engine.context

import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalActions
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.context.impl.AgentContextImpl
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.events.EventQueue
import it.unibo.jakta.agents.bdi.engine.generation.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.engine.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

/**
 * The Context is the actual state of a BDI Agent's structures.
 */
interface AgentContext {
    /** [BeliefBase] of the BDI Agent */
    val beliefBase: BeliefBase

    /** [it.unibo.jakta.agents.bdi.engine.events.Event]s on which the BDI Agent reacts */
    val events: EventQueue

    /** [it.unibo.jakta.agents.bdi.engine.plans.Plan]s collection of the BDI Agent */
    val planLibrary: PlanLibrary

    val internalActions: Map<String, InternalAction>

    val generationProcesses: GenerationProcessRegistry

    val intentions: IntentionPool

    val admissibleGoals: Set<AdmissibleGoal>

    val admissibleBeliefs: Set<AdmissibleBelief>

    fun copy(
        beliefBase: BeliefBase = this.beliefBase,
        events: EventQueue = this.events,
        planLibrary: PlanLibrary = this.planLibrary,
        internalActions: Map<String, InternalAction> = this.internalActions,
        generationProcesses: GenerationProcessRegistry = this.generationProcesses,
        intentions: IntentionPool = this.intentions,
        admissibleGoals: Set<AdmissibleGoal> = this.admissibleGoals,
        admissibleBeliefs: Set<AdmissibleBelief> = this.admissibleBeliefs,
    ): AgentContext =
        AgentContextImpl(
            beliefBase,
            events,
            planLibrary,
            internalActions,
            generationProcesses,
            intentions,
            admissibleGoals,
            admissibleBeliefs,
        )

    companion object {
        fun of(
            beliefBase: BeliefBase = BeliefBase.empty(),
            events: EventQueue = emptyList(),
            planLibrary: PlanLibrary = PlanLibrary.empty(),
            internalActions: Map<String, InternalAction> = InternalActions.default(),
            generationProcesses: GenerationProcessRegistry = GenerationProcessRegistry.empty(),
            intentions: IntentionPool = IntentionPool.empty(),
            admissibleGoals: Set<AdmissibleGoal> = emptySet(),
            admissibleBeliefs: Set<AdmissibleBelief> = emptySet(),
        ): AgentContext =
            AgentContextImpl(
                beliefBase,
                events,
                planLibrary,
                internalActions,
                generationProcesses,
                intentions,
                admissibleGoals,
                admissibleBeliefs,
            )
    }
}
