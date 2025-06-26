package it.unibo.jakta.agents.bdi.engine.actions.impl

import it.unibo.jakta.agents.bdi.engine.actions.ActionSignature
import it.unibo.jakta.agents.bdi.engine.actions.InternalAction
import it.unibo.jakta.agents.bdi.engine.actions.InternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.InternalResponse
import it.unibo.jakta.agents.bdi.engine.actions.effects.AgentChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.BeliefChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.IntentionChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.Pause
import it.unibo.jakta.agents.bdi.engine.actions.effects.PlanChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.Sleep
import it.unibo.jakta.agents.bdi.engine.actions.effects.Stop
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.engine.context.ContextUpdate.REMOVAL
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.plans.Plan

abstract class AbstractInternalAction(
    override val actionSignature: ActionSignature,
) : AbstractAction<AgentChange, InternalResponse, InternalRequest>(actionSignature),
    InternalAction {
    constructor(name: String) : this(name.toActionSignature())

    constructor(name: String, arity: Int) : this(name.toActionSignature(arity))

    constructor(name: String, vararg parameterNames: String) :
        this(name.toActionSignature(parameterNames.size, parameterNames.toList()))

    constructor(name: String, parameterNames: List<String>) :
        this(name.toActionSignature(parameterNames.size, parameterNames.toList()))

    override fun addBelief(belief: Belief) {
        effects.add(BeliefChange(belief, ADDITION))
    }

    override fun removeBelief(belief: Belief) {
        effects.add(BeliefChange(belief, REMOVAL))
    }

    override fun addIntention(intention: Intention) {
        effects.add(IntentionChange(intention, ADDITION))
    }

    override fun removeIntention(intention: Intention) {
        effects.add(IntentionChange(intention, REMOVAL))
    }

    override fun addEvent(event: Event) {
        effects.add(EventChange(event, ADDITION))
    }

    override fun removeEvent(event: Event) {
        effects.add(EventChange(event, REMOVAL))
    }

    override fun addPlan(plan: Plan) {
        effects.add(PlanChange(plan, ADDITION))
    }

    override fun removePlan(plan: Plan) {
        effects.add(PlanChange(plan, REMOVAL))
    }

    override fun stopAgent() {
        effects.add(Stop())
    }

    override fun sleepAgent(millis: Long) {
        effects.add(Sleep(millis))
    }

    override fun pauseAgent() {
        effects.add(Pause())
    }

    override fun toString(): String = "InternalAction(${signature.name}, ${signature.arity})"
}
