package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.AgentScope
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.playground.BlocksWorldLiterate.gripperOperator
import it.unibo.jakta.playground.Literals.Block
import kotlin.reflect.KProperty

object OwnName {
    operator fun getValue(
        thisRef: Any?,
        property: KProperty<*>,
    ) = property.name
}

object Literals {
    val Block: String by OwnName
}

const val GOAL = """
    - `ontable(a)`
    - `on(d, a)`
    - `on(c, d)`
    - `clear(c)`
    - `ontable(b)`
    - `clear(b)`
    """

object BlocksWorldLiterate {
    fun MasScope.gripperOperator() =
        agent("gripperOperator") {
            goals { achieve(GOAL) }
            beliefs {
                fact { "on"("b", "a") }
                fact { "ontable"("a") }
                fact { "on"("c", "b") }
                fact { "clear"("c") }
                fact { "holding"("d") }
            }
            plans { plans() }
        }

    fun AgentScope.plans() = plans {
        +achieve("pickup"(Block)) onlyIf {
            "ontable"(Block).fromSelf and
                "gripperEmpty".fromSelf and
                "clear"(Block).fromSelf
        } then {
            add("holding"(Block))
            remove("gripperEmpty".fromSelf)
            remove("clear"(Block).fromSelf)
            remove("ontable"(Block).fromSelf)
        }

        +achieve("unstack"(X, Y)) onlyIf {
            "on"(X, Y).fromSelf and
                "gripperEmpty".fromSelf and
                "clear"(X).fromSelf
        } then {
            add("holding"(X).fromSelf)
            remove("gripperEmpty".fromSelf)
            remove("clear"(X).fromSelf)
            add("clear"(Y).fromSelf)
            remove("on"(X, Y).fromSelf)
        }

        +achieve("putdown"(Block)) onlyIf {
            "holding"(Block).fromSelf
        } then {
            add("ontable"(Block).fromSelf)
            remove("holding"(Block).fromSelf)
            add("gripperEmpty".fromSelf)
            add("clear"(Block).fromSelf)
        }

        +achieve("stack"(X, Y)) onlyIf {
            "holding"(X).fromSelf and "clear"(Y).fromSelf
        } then {
            add("on"(X, Y).fromSelf)
            remove("holding"(X).fromSelf)
            add("gripperEmpty".fromSelf)
            remove("clear"(Y).fromSelf)
            add("clear"(X).fromSelf)
        }

        +achieve(GOAL) given { generate = true }
    }
}

fun main() =
    mas {
        gripperOperator()
        generationStrategy = LMGenerationStrategy.react {
            remark(
                "Let's say you want to unstack block a from block b," +
                    " then you'll say to me: `achieve(unstack(a, b))`.",
            )
        }
    }.start()
