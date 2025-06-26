package it.unibo.jakta.agents.bdi.engine.actions.effects

import it.unibo.jakta.agents.bdi.engine.Jakta.capitalize
import it.unibo.jakta.agents.bdi.engine.Jakta.removeSource
import it.unibo.jakta.agents.bdi.engine.Jakta.source
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.context.ContextUpdate
import it.unibo.jakta.agents.bdi.engine.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.engine.context.ContextUpdate.REMOVAL
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.triggerFormatter
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.events.AgentEvent
import it.unibo.jakta.agents.bdi.engine.logging.events.BdiEvent.Companion.eventType
import it.unibo.jakta.agents.bdi.engine.logging.events.EventType
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("AgentChange")
sealed interface AgentChange :
    SideEffect,
    AgentEvent

@Serializable
@SerialName("InternalChange")
sealed interface InternalChange : AgentChange {
    val changeType: ContextUpdate

    companion object {
        val changeTypeDescription: (ContextUpdate) -> String
            get() = { changeType ->
                when (changeType) {
                    REMOVAL -> "Removed"
                    ADDITION -> "Added"
                }
            }
    }
}

@Serializable
@SerialName("BeliefChange")
data class BeliefChange(
    val belief: Belief,
    override val changeType: ContextUpdate,
    override val eventType: EventType,
    override val description: String?,
) : InternalChange {
    constructor(
        belief: Belief,
        changeType: ContextUpdate,
    ) : this(
        belief,
        changeType,
        EventType("Belief${changeType.name.lowercase().capitalize()}"),
        "${InternalChange.changeTypeDescription(changeType)} ${belief.removeSource()} from source ${belief.source()}",
    )
}

@Serializable
@SerialName("IntentionChange")
data class IntentionChange(
    val intention: Intention,
    override val changeType: ContextUpdate,
    override val eventType: EventType,
    override val description: String?,
) : InternalChange {
    constructor(
        intention: Intention,
        changeType: ContextUpdate,
    ) : this(
        intention,
        changeType,
        EventType("Intention${changeType.name.lowercase().capitalize()}"),
        "${InternalChange.changeTypeDescription(changeType)} intention ${intention.id.id}",
    )
}

@Serializable
@SerialName("EventChange")
data class EventChange(
    val event: Event,
    override val changeType: ContextUpdate,
    override val eventType: EventType,
    override val description: String?,
) : InternalChange {
    constructor(event: Event, changeType: ContextUpdate) : this(
        event,
        changeType,
        EventType("Event${changeType.name.lowercase().capitalize()}"),
        "${InternalChange.changeTypeDescription(changeType)} ${eventType(
            event,
        )} event: ${triggerFormatter.format(event.trigger)}",
    )
}

@Serializable
@SerialName("PlanChange")
data class PlanChange(
    val plan: Plan,
    override val changeType: ContextUpdate,
    override val eventType: EventType,
    override val description: String?,
) : InternalChange {
    constructor(plan: Plan, changeType: ContextUpdate) : this(
        plan,
        changeType,
        EventType("Plan${changeType.name.lowercase().capitalize()}"),
        "${InternalChange.changeTypeDescription(changeType)} plan: ${triggerFormatter.format(
            plan.trigger,
        )} to the plan library",
    )
}

@Serializable
@SerialName("ActivityChange")
sealed interface ActivityChange : AgentChange

@Serializable
@SerialName("Sleep")
data class Sleep(
    val millis: Long,
    override val description: String?,
) : ActivityChange {
    constructor(millis: Long) : this(
        millis,
        "Agent's controller entered sleep state for $millis milliseconds",
    )
}

@Serializable
@SerialName("Stop")
data class Stop(
    override val description: String?,
) : ActivityChange {
    constructor() : this("Agent's controller entered stop state")
}

@Serializable
@SerialName("Pause")
data class Pause(
    override val description: String?,
) : ActivityChange {
    constructor() : this("Agent's controller entered pause state")
}

@Serializable
@SerialName("AdmissibleBeliefChange")
data class AdmissibleBeliefChange(
    val belief: AdmissibleBelief,
    val changeType: ContextUpdate,
    override val description: String?,
) : AgentEvent {
    constructor(belief: AdmissibleBelief, changeType: ContextUpdate) : this(
        belief,
        changeType,
        "${InternalChange.changeTypeDescription(
            changeType,
        )} admissible belief: ${termFormatter.format(belief.rule.head)}",
    )
}

@Serializable
@SerialName("AdmissibleGoalChange")
data class AdmissibleGoalChange(
    val goal: AdmissibleGoal,
    val changeType: ContextUpdate,
    override val description: String?,
) : AgentEvent {
    constructor(goal: AdmissibleGoal, changeType: ContextUpdate) : this(
        goal,
        changeType,
        "${InternalChange.changeTypeDescription(changeType)} admissible goal: ${triggerFormatter.format(goal.trigger)}",
    )
}
