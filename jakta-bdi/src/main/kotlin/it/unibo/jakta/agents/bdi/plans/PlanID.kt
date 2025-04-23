package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.tuprolog.core.Struct

/**
 * Represents the context of a plan.
 */
data class PlanID(val trigger: Trigger, val guard: Struct)
