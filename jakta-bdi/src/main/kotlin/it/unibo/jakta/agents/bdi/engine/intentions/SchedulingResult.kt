package it.unibo.jakta.agents.bdi.engine.intentions

data class SchedulingResult(
    val newIntentionPool: IntentionPool,
    val intentionToExecute: Intention,
)
