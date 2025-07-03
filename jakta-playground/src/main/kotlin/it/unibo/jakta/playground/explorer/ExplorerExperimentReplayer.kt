package it.unibo.jakta.playground.explorer

import com.aallam.openai.api.chat.ChatMessage
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.engine.Jakta.separator
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.DSLExtensions.lmGeneration
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMMessageReceived
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters.metaPlanFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.DefaultFilters.printActionFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder.systemPrompt
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting.DefaultPromptBuilder.userPromptWithHintsAndRemarks
import it.unibo.jakta.playground.MockGenerationStrategy.createLMGenStrategyWithMockedAPI
import it.unibo.jakta.playground.ModuleLoader
import it.unibo.jakta.playground.ModuleLoader.jsonModule
import it.unibo.jakta.playground.evaluation.FileProcessor.processFile
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractAgentLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractPgpLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.findMasLogFile
import it.unibo.jakta.playground.explorer.agents.ExplorerRobot.explorerRobot
import it.unibo.jakta.playground.gridworld.environment.GridWorldDsl.gridWorld
import java.util.UUID

class ExperimentReplayer : CliktCommand() {
    val logToFile: Boolean by option()
        .flag()
        .help("Whether to run an experiment and save the new execution trace as a separate experiment.")

    val expDir: String by option()
        .help("The directory from which the previously recorded LLM responses will be taken.")
        .required()

    init {
        ModuleLoader.loadModules()
    }

    override fun run() {
        val expName = UUID.randomUUID().toString()
        val lmResponses = getChatMessages(expDir).mapNotNull { it.content }
        if (lmResponses.isEmpty()) {
            println("No LLM responses found. Cannot replay experiment.")
        } else {
            mas {
                modules = listOf(jsonModule)
                loggingConfig =
                    LoggingConfig(
                        logDir = extractDirectoryPath(expDir) + separator + expName,
                        logToFile = logToFile,
                    )
                gridWorld()
                explorerRobot(strategy = createLMGenStrategyWithMockedAPI(lmResponses))

                lmGeneration {
                    contextFilters = listOf(metaPlanFilter, printActionFilter)
                    systemPromptBuilder = systemPrompt
                    userPromptBuilder = userPromptWithHintsAndRemarks
                }
            }.start()
        }
    }

    companion object {
        private fun getChatMessages(expDir: String): List<ChatMessage> {
            val history = mutableListOf<ChatMessage>()
            val masLogFile = findMasLogFile(expDir) ?: return emptyList()
            extractAgentLogFiles(expDir, masLogFile).forEach { agentLogFile ->
                extractPgpLogFiles(expDir, agentLogFile).forEach { pgpLogFile ->
                    processFile(pgpLogFile) { logEntry ->
                        val event = logEntry.message.event
                        if (event is LMMessageReceived) {
                            event.chatMessage.let { history.add(it) }
                        }
                        true
                    }
                }
            }
            return history
        }

        private fun extractDirectoryPath(pathString: String): String {
            val normalizedPath = pathString.replace('\\', '/')

            val pattern = """(.+)/[^/]+$""".toRegex()
            val matchResult = pattern.find(normalizedPath.trim())

            val dirPath = matchResult?.groupValues?.get(1) ?: normalizedPath

            return if (dirPath.endsWith("/")) dirPath else "$dirPath/"
        }
    }
}

fun main(args: Array<String>): Unit =
    ExperimentReplayer()
        .context { terminal = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, interactive = true) }
        .main(args)
