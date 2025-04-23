package it.unibo.jakta.agents.bdi.context.impl

import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.events.EventQueue
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.plangeneration.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

/** Implementation of Agent's [AgentContext] */
internal class AgentContextImpl(
    override val beliefBase: BeliefBase,
    override val events: EventQueue,
    override val planLibrary: PlanLibrary,
    override val internalActions: Map<String, InternalAction>,
    override val generationProcesses: GenerationProcessRegistry,
    override val intentions: IntentionPool = IntentionPool.empty(),
    override val admissibleGoals: Set<AdmissibleGoal> = emptySet(),
    override val admissibleBeliefs: Set<AdmissibleBelief> = emptySet(),
) : AgentContext {
    override fun toString(): String = """
    AgentContext {
        beliefBase = [$beliefBase]
        events = $events
        planLibrary = [${planLibrary.plans}]
        internalActions = [$internalActions]
        generationProcesses = [$generationProcesses]
        intentions = [$intentions]
        admissibleGoals = [$admissibleGoals]
        admissibleBeliefs = [$admissibleBeliefs]
    }
    """.trimIndent()
}
