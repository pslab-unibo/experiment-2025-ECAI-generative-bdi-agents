package it.unibo.jakta.agents.bdi.engine.events

import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.triggerFormatter
import kotlinx.serialization.Serializable

@Serializable
data class AdmissibleGoal(
    val trigger: Trigger,
) {
    override fun toString() = "${triggerFormatter.format(trigger)}: ${trigger.purpose}"
}
