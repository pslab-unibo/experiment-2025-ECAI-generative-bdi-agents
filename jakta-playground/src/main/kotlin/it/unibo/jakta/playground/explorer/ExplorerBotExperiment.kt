package it.unibo.jakta.playground.explorer

import it.unibo.jakta.agents.bdi.dsl.loggingConfig
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.dsl.DSLExtensions.oneStepGeneration
import it.unibo.jakta.playground.experiment.Experiment
import it.unibo.jakta.playground.explorer.ExplorerBot.explorerBot
import it.unibo.jakta.playground.explorer.ExplorerBot.getDirectionToMove
import it.unibo.jakta.playground.explorer.ExplorerBot.move
import it.unibo.jakta.playground.explorer.gridworld.GridWorld

class ExplorerBotExperiment : Experiment() {
    override fun createMas(
        logConfig: LoggingConfig,
        genStrat: GenerationStrategy?,
    ) = mas {
        executionStrategy = ExecutionStrategy.oneThreadPerAgent()
        generationStrategy = genStrat
        loggingConfig = logConfig

        explorerBot()

        environment {
            from(GridWorld())
            actions {
                action(move).meaning {
                    "move in the given direction: ${args[0]}"
                }
                action(getDirectionToMove).meaning {
                    "provides a Direction free of obstacles where the agent can then move"
                }
            }
        }
    }

    override fun createLoggingConfig(expName: String) = loggingConfig {
        logToFile = this@ExplorerBotExperiment.logToFile
        logToConsole = this@ExplorerBotExperiment.logToConsole
        logLevel = this@ExplorerBotExperiment.logLevel.level
        logDir = "${this@ExplorerBotExperiment.logDir}/$modelId/$expName"
    }

    override fun createGenerationStrategy() =
        oneStepGeneration {
            url = lmServerUrl
            token = lmServerToken
            model = modelId
            maxTokens = this@ExplorerBotExperiment.maxTokens
            temperature = this@ExplorerBotExperiment.temperature
        }
}
