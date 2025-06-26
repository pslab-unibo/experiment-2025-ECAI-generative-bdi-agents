package it.unibo.jakta.playground.printer

import it.unibo.jakta.agents.bdi.dsl.goals.TriggerMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.DSLExtensions.oneStepGeneration
import it.unibo.jakta.playground.ModuleLoader.jsonModule

//        generationStrategy = createOneStepStrategyWithMockedAPI(listOf(""))

fun main() =
    mas {
        oneStepGeneration {
            model = "openai/gpt-4.1"
            temperature = 0.5
            maxTokens = 1024
        }
        loggingConfig = LoggingConfig(logToFile = true)
        modules = listOf(jsonModule)

        agent("Printer") {
            goals {
                +achieve("print"(0, 10))

                admissible {
                    +achieve("print_numbers"("start", "end")).meaning {
                        "Print the numbers from ${args[0]} to ${args[1]}"
                    }
                }
            }
            plans {
                +achieve("print"(X, Y)) then {
                    generate("print_numbers"(X, Y))
                }
            }
        }
    }.start()
