package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.actions.effects.AgentChange
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.plans.Plan

interface InternalAction : Action<AgentChange, InternalResponse, InternalRequest> {
    fun addBelief(belief: Belief)

    fun removeBelief(belief: Belief)

    fun addIntention(intention: Intention)

    fun removeIntention(intention: Intention)

    fun addEvent(event: Event)

    fun removeEvent(event: Event)

    fun addPlan(plan: Plan)

    fun removePlan(plan: Plan)

    fun stopAgent()

    fun sleepAgent(millis: Long)

    fun pauseAgent()
}
