package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.context.ContextUpdate
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plans.PlanID

class EventManager(private val logger: KLogger?) {

    fun updateEvents(
        context: AgentContext,
        planID: PlanID,
        intention: Intention,
        planGenResult: PlanGenerationResult,
    ): List<Event> =
        if (planGenResult.generationState.isGenerationEndConfirmed) {
            context.events
        } else {
            val newEvent = Event.of(planID.trigger, intention)
            context.events + newEvent.also {
                logger?.implementation(EventChange(newEvent, ContextUpdate.ADDITION))
            }
        }
}
