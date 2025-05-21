package it.unibo.jakta.agents.bdi.engine.plans.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("Plan")
internal data class PlanImpl(
    override val trigger: Trigger,
    override val guard: SerializableStruct,
    @Transient
    override val id: PlanID = PlanID(trigger, guard),
    override val goals: List<Goal>,
) : AbstractPlan() {
    override fun applicablePlan(
        event: Event,
        beliefBase: BeliefBase,
    ): Plan =
        createApplicablePlan(event, beliefBase)?.let { (actualGuard, actualGoals) ->
            Plan.of(id, event.trigger, actualGuard, actualGoals)
        } ?: this
}
