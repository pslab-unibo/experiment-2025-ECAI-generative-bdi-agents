package it.unibo.jakta.agents.bdi.actions.effects

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.context.ContextUpdate
import it.unibo.jakta.agents.bdi.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.context.ContextUpdate.REMOVAL
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.BdiEvent.Companion.eventDescription
import it.unibo.jakta.agents.bdi.plans.Plan

sealed interface AgentChange : SideEffect

sealed interface InternalChange : AgentChange {
    val changeType: ContextUpdate
}

sealed interface ActivityChange : AgentChange

data class BeliefChange(
    val belief: Belief,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name: String = "Belief${changeType.name.lowercase().capitalize()}" +
        "From${belief.source()}"

    override val description = when (changeType) {
        REMOVAL -> "Removed $belief from source ${belief.source()}"
        ADDITION -> "Added $belief from source ${belief.source()}"
    }

    companion object {
        fun Belief.source() = rule.head.args.first().castToStruct().args.first().toString().capitalize()
    }
}

data class IntentionChange(
    val intention: Intention,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name = "Intention${changeType.name.lowercase().capitalize()}"

    override val description = "Intention ${intention.recordStack}"
}

data class EventChange(
    val event: Event,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name = "Event${changeType.name.lowercase().capitalize()}"

    override val description = when (changeType) {
        ADDITION -> eventDescription(event, "created")
        REMOVAL -> eventDescription(event, "deleted")
    }
}

data class PlanChange(
    val plan: Plan,
    override val changeType: ContextUpdate,
) : InternalChange {
    override val name = "Plan${changeType.name.lowercase().capitalize()}"

    override val description = when (changeType) {
        ADDITION -> "Plan: ${plan.trigger.value} added to the plan library"
        REMOVAL -> "Plan ${plan.trigger.value} removed from the plan library"
    }
}

data class Sleep(val millis: Long) : ActivityChange {
    override val description = "Agent's controller entered sleep state for $millis ms"
}

class Stop : ActivityChange {
    override val description = "Agent's controller entered stop state"
}

class Pause : ActivityChange {
    override val description = "Agent's controller entered pause state"
}
