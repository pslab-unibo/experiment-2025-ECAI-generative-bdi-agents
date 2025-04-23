package it.unibo.jakta.agents.bdi.executionstrategies.feedback

import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.tuprolog.core.Struct

data class PlanApplicabilityResult(
    val trigger: Trigger? = null,
    val guards: Map<Struct, Boolean>? = null,
    val error: String? = null,
)
