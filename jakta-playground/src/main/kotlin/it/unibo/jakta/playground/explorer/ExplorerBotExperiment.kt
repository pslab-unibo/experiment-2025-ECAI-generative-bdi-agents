package it.unibo.jakta.playground.explorer

import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.environment.EnvironmentScope
import it.unibo.jakta.agents.bdi.dsl.loggingConfig
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.dsl.DSLExtensions.oneStepGeneration
import it.unibo.jakta.playground.experiment.Experiment
import it.unibo.jakta.playground.explorer.ExplorerBot.explorerBot
import it.unibo.jakta.playground.explorer.gridworld.GridWorld
import it.unibo.jakta.playground.getDirectionToMove
import it.unibo.jakta.playground.move

class ExplorerBotExperiment : Experiment() {
    override fun createMas(
        logConfig: LoggingConfig,
        genStrat: GenerationStrategy?,
    ) = mas {
        executionStrategy = ExecutionStrategy.oneThreadPerAgent()
        generationStrategy = genStrat
        loggingConfig = logConfig

        environment {
            from(GridWorld())
            actions {
                action(move).meaning {
                    "move in the given direction: ${args[0]}"
                }
                action(getDirectionToMove).meaning {
                    "provides a Direction free of obstacles where the agent can then move"
                }
                removeAgents(agents.map { it.name })
            }
        }

        explorerBot()

        timeoutAgent(timeout)
    }

    override fun createLoggingConfig(expName: String) = loggingConfig {
        logServerURL = this@ExplorerBotExperiment.logServerURL
        logToFile = this@ExplorerBotExperiment.logToFile
        logToConsole = this@ExplorerBotExperiment.logToConsole
        logLevel = this@ExplorerBotExperiment.logLevel.level
        logDir = "${this@ExplorerBotExperiment.logDir}/$expName"
    }

    override fun createGenerationStrategy() = oneStepGeneration {
        url = lmServerUrl
        token = lmServerToken
        model = modelId
        maxTokens = this@ExplorerBotExperiment.maxTokens
        temperature = this@ExplorerBotExperiment.temperature
    }

    companion object {
        fun EnvironmentScope.removeAgents(agentIds: List<String>) =
            actions {
                action("removeAgents", 0) {
                    agentIds.forEach {
                        println("[$sender] Removed $it")
                        removeAgent(it)
                    }
                }
            }

        fun MasScope.timeoutAgent(timeoutSeconds: Int) =
            agent("timeout") {
                goals { +achieve("timeout"(timeoutSeconds * 1000)) }
                plans {
                    +achieve("timeout"(N)) then {
                        execute("print"("Sleeping for", N))
                        execute("sleep"(N))
                        execute("removeAgents")
                    }
                }
            }
    }
}
