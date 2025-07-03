package it.unibo.jakta.playground.explorer

import it.unibo.jakta.agents.bdi.dsl.actions.ActionMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.loggingConfig
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.Remark
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.DSLExtensions.lmGeneration
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters.metaPlanFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters.printActionFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder.systemPrompt
import it.unibo.jakta.playground.AbstractExperiment
import it.unibo.jakta.playground.ModuleLoader.jsonModule
import it.unibo.jakta.playground.explorer.agents.ExplorerRobot.explorerRobot
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.getDirectionToMove
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.move
import it.unibo.jakta.playground.gridworld.environment.GridWorldEnvironment
import kotlin.time.Duration.Companion.seconds

class ExplorerExperiment : AbstractExperiment() {
    val explorerRemarks =
        listOf(
            Remark(
                """
                In order to move to a Direction, you first need to get that direction with the getDirectionToMove action.
                """.trimIndent(),
            ),
            Remark(
                """
                The there_is belief is only present in the belief base if the agent is adjacent to an object. At the start, the Home object is not in the perceptive field of the agent, so you need to create plans that handle the absence of the there_is belief.
                """.trimIndent(),
            ),
        )

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
        lmGeneration {
            url = lmServerUrl
            token = lmServerToken
            model = modelId
            maxTokens = this@ExplorerExperiment.maxTokens
            temperature = this@ExplorerExperiment.temperature
            requestTimeout = 240.seconds
            systemPromptBuilder = systemPrompt
            userPromptBuilder = this@ExplorerExperiment.promptType.builder
            contextFilters = listOf(metaPlanFilter, printActionFilter)

            remarks(explorerRemarks)
        }
}
