package it.unibo.jakta.agents.bdi.events

import it.unibo.jakta.agents.bdi.Jakta.termFormatter

data class AdmissibleGoal(val trigger: Trigger) {
    override fun toString() = "${termFormatter.format(trigger.value)}: ${trigger.purpose}"
}
