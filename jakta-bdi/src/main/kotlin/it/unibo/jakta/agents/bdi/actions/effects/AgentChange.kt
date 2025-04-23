package it.unibo.jakta.agents.bdi.actions.effects

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.context.ContextUpdate
import it.unibo.jakta.agents.bdi.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.context.ContextUpdate.REMOVAL
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.events.BdiEvent.Companion.eventType
import it.unibo.jakta.agents.bdi.logging.events.BdiEvent.Companion.triggerDescription
import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface AgentChange : SideEffect

sealed interface InternalChange : AgentChange {
    val changeType: ContextUpdate

    val changeTypeDescription: String
        get() = when (changeType) {
            REMOVAL -> "Removed"
            ADDITION -> "Added"
        }
}

data class BeliefChange(
    val belief: Belief,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name: String = "Belief${changeType.name.lowercase().capitalize()}" +
        "From${belief.source()}"

    override val description =
        "$changeTypeDescription ${belief.rule.head.removeSource()} from source ${belief.source()}"

    companion object {
        fun Belief.source() = rule.head.args.first().castToStruct().args.first().toString().capitalize()
    }
}

data class IntentionChange(
    val intention: Intention,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name = "Intention${changeType.name.lowercase().capitalize()}"

    override val description = "$changeTypeDescription intention ${intention.id.id}"
}

data class EventChange(
    val event: Event,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name = "Event${changeType.name.lowercase().capitalize()}"

    override val description =
        "$changeTypeDescription ${eventType(event)} event ${triggerDescription(event.trigger)}"

    override val metadata = super.metadata + buildMap {
        put("changeType", changeType)
        put("trigger", event.trigger)
        put("intention", event.intention)
    }
}

data class PlanChange(
    val plan: Plan,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name = "Plan${changeType.name.lowercase().capitalize()}"

    override val description = "$changeTypeDescription plan: ${termFormatter.format(
        plan.trigger.value,
    )} to the plan library"

    override val metadata = super.metadata + buildMap {
        put("changeType", changeType)
        put("trigger", plan.trigger)
        put("guard", plan.guard)
        put("goals", plan.goals)
    }
}

sealed interface ActivityChange : AgentChange

data class Sleep(val millis: Long) : ActivityChange {
    override val description = "Agent's controller entered sleep state for $millis ms"
}

class Stop : ActivityChange {
    override val description = "Agent's controller entered stop state"
}

class Pause : ActivityChange {
    override val description = "Agent's controller entered pause state"
}
