package it.unibo.jakta.playground.domesticrobot.environment

import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.actions.ExternalActionScope
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ActionFailure
import it.unibo.jakta.agents.bdi.engine.executionstrategies.feedback.ActionSuccess
import it.unibo.jakta.agents.bdi.engine.messages.Achieve
import it.unibo.jakta.agents.bdi.engine.messages.Message
import it.unibo.jakta.agents.bdi.engine.messages.Tell
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct

object HouseDsl {
    fun MasScope.houseEnvironment() =
        environment {
            from(HouseEnvironment())
            actions {
                action("open", "an openable object") { executeHouseAction() }
                action("close", "a closeable object") { executeHouseAction() }
                action("pick", "something") { executeHouseAction() }
                action("hand_in", "something") { executeHouseAction() }
                action("sip", "a drinkable object") { executeHouseAction() }
                action("move_towards", "an object") { executeHouseAction() }
                action("deliver", "an object", "in a given quantity") { executeHouseAction() }

                action("send", 3) {
                    val receiver: Atom = argument(0)
                    val type: Atom = argument(1)
                    val message: Struct = argument(2)
                    when (type.value) {
                        "tell" -> {
                            sendMessage(receiver.value, Message(this.sender, Tell, message))
                            addFeedback(ActionSuccess.GenericActionSuccess(actionSignature, arguments))
                        }
                        "achieve" -> {
                            sendMessage(
                                receiver.value,
                                Message(this.sender, Achieve, message),
                            )
                            addFeedback(ActionSuccess.GenericActionSuccess(actionSignature, arguments))
                        }
                    }
                }
            }
        }

    private fun ExternalActionScope.executeHouseAction() {
        val env = environment as? HouseEnvironment
        if (env != null) {
            val res = env.parseAction(actionName)
            res?.let { updateData("state" to res) }

            val feedback =
                if (res != null) {
                    ActionSuccess.GenericActionSuccess(actionSignature, arguments)
                } else {
                    ActionFailure.GenericActionFailure(actionSignature, arguments)
                }

            addFeedback(feedback)
        } else {
            val feedback =
                ActionFailure.GenericActionFailure(
                    actionSignature,
                    arguments,
                    "Only HouseEnvironment is supported",
                )
            addFeedback(feedback)
        }
    }
}
