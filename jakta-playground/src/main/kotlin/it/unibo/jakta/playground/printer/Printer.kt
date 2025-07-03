package it.unibo.jakta.playground.printer

import it.unibo.jakta.agents.bdi.dsl.goals.TriggerMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.DSLExtensions.lmGeneration
import it.unibo.jakta.playground.ModuleLoader.jsonModule
import it.unibo.jakta.playground.Utils.readTokenFromEnv

fun main() =
    mas {
        lmGeneration {
            model = "deepseek/deepseek-chat-v3-0324:free"
            temperature = 0.5
            url = "https://openrouter.ai/api/v1/"
            token = readTokenFromEnv()
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
                    generatePlan("print_numbers"(X, Y))
                }
            }
        }
    }.start()
