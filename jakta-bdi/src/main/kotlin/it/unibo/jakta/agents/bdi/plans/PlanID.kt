package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

data class PlanID(val trigger: Trigger, val context: Struct = Truth.TRUE)
