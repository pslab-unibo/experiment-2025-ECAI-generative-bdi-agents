package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.mas

fun myAction(hello: String, world: Int): String {
    return "Processed $hello with value $world"
}

fun main() {
    mas {
        agent("test") {
            beliefs {
                fact { "" }
                admissible {
                    fact { "test" }
                }
            }
            actions {
                action(::myAction)
            }
            goals {
                achieve("test")

                admissible {
                    achieve("test")
                }
            }
        }
    }
}
