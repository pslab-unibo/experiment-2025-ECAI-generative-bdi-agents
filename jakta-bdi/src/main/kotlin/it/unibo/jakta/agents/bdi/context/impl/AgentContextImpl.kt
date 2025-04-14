package it.unibo.jakta.agents.bdi.context.impl

import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.EventQueue
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.plangeneration.pool.GenerationRequestPool
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.nlp.literateprolog.LiteratePrologTemplate

/** Implementation of Agent's [AgentContext] */
internal class AgentContextImpl(
    override val beliefBase: BeliefBase,
    override val events: EventQueue,
    override val planLibrary: PlanLibrary,
    override val internalActions: Map<String, InternalAction>,
    override val generationRequests: GenerationRequestPool = GenerationRequestPool.empty(),
    override val intentions: IntentionPool = IntentionPool.empty(),
    override val templates: List<LiteratePrologTemplate>,
) : AgentContext {
    override fun toString(): String = """
    AgentContext {
        beliefBase = [$beliefBase]
        events = $events
        planLibrary = [${planLibrary.plans}]
        internalActions = [$internalActions]
        generationRequests = [$generationRequests]
        intentions = [$intentions]
        templates = [$templates]
    }
    """.trimIndent()
}
