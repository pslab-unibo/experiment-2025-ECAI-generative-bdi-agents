package it.unibo.jakta.playground.explorer.gridworld

import it.unibo.jakta.agents.bdi.logging.events.LogEvent

class ObjectReachedEvent(objectName: String) : LogEvent {
    override val description = "Reached object $objectName"
}
