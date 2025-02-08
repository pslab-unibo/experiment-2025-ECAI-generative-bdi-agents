package it.unibo.jakta.agents.bdi.logging

sealed interface LogEvent {
    val name: String get() = this.javaClass.simpleName
    val description: String
}

data class ReasoningCycleStart(
    override val name: String = "NewReasoningCycle",
    val cycleCount: Int,
) : LogEvent {
    override val description = "Reasoning cycle $cycleCount started"
}
