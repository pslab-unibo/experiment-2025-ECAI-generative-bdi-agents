package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

/**
 * The identifier is built considering the context of a plan.
 */
data class PlanID(
    val trigger: Trigger,
    val guard: Struct,
) {
    companion object {
        fun of(trigger: Trigger, guard: Struct = Truth.TRUE): PlanID = PlanID(trigger, guard)
    }
}
