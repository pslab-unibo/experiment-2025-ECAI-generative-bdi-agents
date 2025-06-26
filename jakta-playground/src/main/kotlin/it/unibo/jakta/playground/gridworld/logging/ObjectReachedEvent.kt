package it.unibo.jakta.playground.gridworld.logging

import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ActionSuccess
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableTerm
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.move
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ObjectReached")
data class ObjectReachedEvent(
    val objectName: String,
    override val providedArguments: List<SerializableTerm>,
) : ActionSuccess {
    override val actionSignature = move.actionSignature

    override val description = "Reached object $objectName"
}
