package it.unibo.jakta.agents.bdi.events

import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter

data class AdmissibleGoal(val trigger: Trigger) {
    override fun toString() =
        "${triggerFormatter.format(trigger)}: ${trigger.purpose}"
}
