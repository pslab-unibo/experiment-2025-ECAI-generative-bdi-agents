package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.actions.Mode
import it.unibo.jakta.agents.bdi.actions.Parameter
import it.unibo.jakta.agents.bdi.actions.Type
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.generationstrategies.GenerationStrategy
import it.unibo.jakta.agents.bdi.messages.Achieve
import it.unibo.jakta.agents.bdi.messages.Message
import it.unibo.jakta.agents.bdi.messages.Tell
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct

const val orderedGoalExecution =
    "Print numbers I...J in this order and stop the agent at the end."

const val agentNeedsToStop =
    "The agent must stop only once, when all the numbers in the sequence are printed."

fun MasScope.printerAgent() =
    agent("printer") {
        goals {
            achieve(orderedGoalExecution(1, 10))
        }
        plans {
            +achieve(orderedGoalExecution(I, J)) given {
                // generate = true
                generationStrategy = GenerationStrategy.oneShot()
                remark(agentNeedsToStop)
            }
        }
    }

fun main() {
    mas {
        environment {
            actions {
                action(
                    name = "send",
                    description = "Sends a message",
                    parameters = listOf(
                        Parameter("receiver", Type.Atom, Mode.Input),
                        Parameter("type", Type.Atom, Mode.Input),
                        Parameter("message", Type.Struct, Mode.Input),
                    ),
                ) {
                    val receiver: Atom = argument(0)
                    val type: Atom = argument(1)
                    val message: Struct = argument(2)
                    when (type.value) {
                        "tell" -> sendMessage(receiver.value, Message(this.sender, Tell, message))
                        "achieve" ->
                            sendMessage(
                                receiver.value,
                                Message(this.sender, Achieve, message),
                            )
                    }
                }
            }
        }
//        generationStrategy = GenerationStrategy.oneShot()
        printerAgent()
    }.start()
}
