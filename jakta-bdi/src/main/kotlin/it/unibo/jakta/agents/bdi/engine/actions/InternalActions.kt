package it.unibo.jakta.agents.bdi.engine.actions

import it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractInternalAction
import it.unibo.tuprolog.core.Substitution

object InternalActions {
    object Print : AbstractInternalAction("print", "message", "payload") {
        override var purpose: String? = "prints a `Message` and its `Payload`"

        override fun action(request: InternalRequest) {
            val payload =
                request.arguments.joinToString(" ") {
                    when {
                        it.isAtom -> it.castToAtom().value
                        else -> it.toString()
                    }
                }
            println("[${request.agent.name}] $payload")
        }
    }

    object Fail : AbstractInternalAction("fail") {
        override var purpose: String? = "makes the agent fail its current intention"

        override fun action(request: InternalRequest) {
            result = Substitution.failed()
        }
    }

    object Stop : AbstractInternalAction("stop") {
        override var purpose: String? = "stops the agent"

        override fun action(request: InternalRequest) {
            stopAgent()
        }
    }

    object Pause : AbstractInternalAction("pause") {
        override var purpose: String? = "pauses the agent"

        override fun action(request: InternalRequest) {
            pauseAgent()
        }
    }

    object Sleep : AbstractInternalAction("sleep", "time") {
        override var purpose: String? = "makes the agent sleep for `Time` milliseconds"

        override fun action(request: InternalRequest) {
            if (request.arguments[0].isInteger) {
                sleepAgent(
                    request.arguments[0]
                        .castToInteger()
                        .value
                        .toLong(),
                )
            }
        }
    }

    fun default() =
        mapOf(
            Print.signature.name to Print,
            Fail.signature.name to Fail,
            Stop.signature.name to Stop,
            Pause.signature.name to Pause,
            Sleep.signature.name to Sleep,
        )
}
