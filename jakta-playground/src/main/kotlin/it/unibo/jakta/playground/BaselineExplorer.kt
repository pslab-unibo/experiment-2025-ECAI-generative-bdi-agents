package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.playground.explorer.ExplorerBot.baselinePlans
import it.unibo.jakta.playground.explorer.ExplorerBot.explorerBot
import it.unibo.jakta.playground.explorer.ExplorerBot.gridWorld

fun main() =
    mas {
        loggingConfig = LoggingConfig()
        gridWorld()
        explorerBot(baselinePlans())
    }.start()
