package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.events.Trigger
import java.util.UUID

data class PlanID(
    val id: String = generateId(),
    val trigger: Trigger, // TODO remove from there, it's superfluous
) {
    companion object {
        fun of(trigger: Trigger): PlanID = PlanID(generateId(), trigger)
        private fun generateId(): String = UUID.randomUUID().toString()
    }
}
