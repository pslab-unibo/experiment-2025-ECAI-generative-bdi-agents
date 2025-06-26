package it.unibo.jakta.playground.explorer.logging

import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ActionSuccess
import it.unibo.jakta.agents.bdi.engine.serialization.modules.SerializableTerm
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.move
import it.unibo.jakta.playground.gridworld.model.Cell
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("MoveActionSuccess")
data class MoveActionSuccess(
    val oldPosition: Cell,
    val newPosition: Cell,
    override val providedArguments: List<SerializableTerm>,
) : ActionSuccess {
    override val actionSignature = move.actionSignature

    override val description = "Moved from $oldPosition to $newPosition"
}
