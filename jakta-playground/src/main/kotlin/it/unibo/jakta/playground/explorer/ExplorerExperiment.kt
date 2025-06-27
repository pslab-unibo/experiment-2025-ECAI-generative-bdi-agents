package it.unibo.jakta.playground.explorer

import it.unibo.jakta.agents.bdi.dsl.actions.ActionMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.loggingConfig
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.DSLExtensions.oneStepGeneration
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters
import it.unibo.jakta.playground.AbstractExperiment
import it.unibo.jakta.playground.ModuleLoader.jsonModule
import it.unibo.jakta.playground.explorer.agents.ExplorerRobot.explorerRobot
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.getDirectionToMove
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.move
import it.unibo.jakta.playground.gridworld.environment.GridWorldEnvironment
import kotlin.time.Duration.Companion.seconds

class ExplorerExperiment : AbstractExperiment() {
    override fun createMas(
        logConfig: LoggingConfig,
        genStrat: GenerationStrategy?,
    ) = mas {
        executionStrategy = ExecutionStrategy.oneThreadPerAgent()
        generationStrategy = genStrat
        loggingConfig = logConfig
        modules = listOf(jsonModule)

        explorerRobot()

        environment {
            from(GridWorldEnvironment())
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

    override fun createLoggingConfig(expName: String) =
        loggingConfig {
            logToFile = this@ExplorerExperiment.logToFile
            logToConsole = this@ExplorerExperiment.logToConsole
            logToServer = this@ExplorerExperiment.logToServer
            logLevel = this@ExplorerExperiment.logLevel.level
            logDir = "${this@ExplorerExperiment.logDir}/$expName"
            logServerUrl = this@ExplorerExperiment.logServerUrl
        }

    override fun createGenerationStrategy() =
        oneStepGeneration {
            url = lmServerUrl
            token = lmServerToken
            model = modelId
            maxTokens = this@ExplorerExperiment.maxTokens
            temperature = this@ExplorerExperiment.temperature
            requestTimeout = 240.seconds
            promptBuilder = this@ExplorerExperiment.promptType.builder
            contextFilters = listOf(DefaultFilters.metaPlanFilter, DefaultFilters.printActionFilter)
        }
}
