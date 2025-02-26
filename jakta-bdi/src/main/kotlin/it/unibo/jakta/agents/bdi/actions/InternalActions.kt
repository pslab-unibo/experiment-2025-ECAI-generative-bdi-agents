package it.unibo.jakta.agents.bdi.actions

import it.unibo.jakta.agents.bdi.actions.impl.AbstractInternalAction
import it.unibo.tuprolog.core.Substitution

object InternalActions {
    object Print : AbstractInternalAction(
        """
        [print(@message, @payload)] prints a [@message] and its [@payload]
        """,
        arity = 2,
    ) {
        override fun action(request: InternalRequest) {
            val payload = request.arguments.joinToString(" ") {
                when {
                    it.isAtom -> it.castToAtom().value
                    else -> it.toString()
                }
            }
            println("[${request.agent.name}] $payload")
        }
    }

    object Fail : AbstractInternalAction(
        """
        [fail] makes the agent fail
        """,
        arity = 0,
    ) {
        override fun action(request: InternalRequest) {
            result = Substitution.failed()
        }
    }

    object Stop : AbstractInternalAction(
        """
        [stop] makes the agent stop
        """,
        arity = 0,
    ) {
        override fun action(request: InternalRequest) {
            stopAgent()
        }
    }

    object Pause : AbstractInternalAction(
        """
        [pause] makes the agent pause 
        """,
        arity = 0,
    ) {
        override fun action(request: InternalRequest) {
            pauseAgent()
        }
    }

    object Sleep : AbstractInternalAction(
        """
        [sleep(@time)] makes the agent sleep for [@time] milliseconds
        """,
        arity = 1,
    ) {
        override fun action(request: InternalRequest) {
            if (request.arguments[0].isInteger) {
                sleepAgent(request.arguments[0].castToInteger().value.toLong())
            }
        }
    }

    fun default() = mapOf(
        Print.signature.name to Print,
        Fail.signature.name to Fail,
        Stop.signature.name to Stop,
        Pause.signature.name to Pause,
        Sleep.signature.name to Sleep,
    )
}
