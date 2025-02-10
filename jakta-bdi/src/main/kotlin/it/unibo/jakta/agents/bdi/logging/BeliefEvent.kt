package it.unibo.jakta.agents.bdi.logging

import it.unibo.jakta.agents.bdi.beliefs.Belief

sealed interface BeliefEvent : LogEvent

data class NewPercept(
    val percept: Belief,
    val source: String,
) : BeliefEvent {
    override val description = "New percept $percept from $source"
}
