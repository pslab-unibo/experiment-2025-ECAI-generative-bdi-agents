package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.playground.MockGenerationStrategy.createOneStepStrategyWithMockedAPI
import it.unibo.jakta.playground.explorer.ExplorerBot.explorerBot
import it.unibo.jakta.playground.explorer.gridworld.GridWorld

fun main() {
    val strategy = createOneStepStrategyWithMockedAPI(listOf(text1)) // text2

    mas {
        loggingConfig = LoggingConfig()
        generationStrategy = strategy

//        oneStepGeneration {
//            url = "http://localhost:8080/"
//        }

        environment {
            from(GridWorld())
            actions {
                action("move", "Direction") {
                    val direction = arguments[0].castToAtom().value
                    updateData("directionToMove" to direction)
                }
            }
        }
        explorerBot()
    }.start()
}
