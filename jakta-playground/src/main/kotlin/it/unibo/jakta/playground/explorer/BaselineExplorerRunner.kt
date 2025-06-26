package it.unibo.jakta.playground.explorer

import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.playground.ModuleLoader.jsonModule
import it.unibo.jakta.playground.explorer.agents.ExplorerRobot.baselinePlans
import it.unibo.jakta.playground.explorer.agents.ExplorerRobot.explorerRobot
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.gridWorld

fun main() {
    val mas =
        mas {
            modules = listOf(jsonModule)
            loggingConfig = LoggingConfig()
            gridWorld()
            explorerRobot(baselinePlans)
        }

    mas.start()

    Thread.sleep(2000)

    mas.stop()
}
