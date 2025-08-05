package it.unibo.jakta.playground.evaluation.apps

import com.aallam.openai.api.chat.ChatMessage
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import it.unibo.jakta.agents.bdi.engine.Jakta.capitalize
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.extractLastComponent
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.extractLastId
import it.unibo.jakta.agents.bdi.engine.serialization.modules.JaktaJsonComponent.json
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_TOKEN
import it.unibo.jakta.playground.ModuleLoader
import it.unibo.jakta.playground.evaluation.FileProcessor.writeToFile
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractAgentLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractPgpLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.findMasLogFile
import it.unibo.jakta.playground.evaluation.MetricsComputer
import it.unibo.jakta.playground.evaluation.plandata.InvocationContext
import it.unibo.jakta.playground.evaluation.plandata.LMPGPInvocation
import kotlinx.serialization.encodeToString
import java.io.File

/**
 * Command-line tool for analyzing Plan Generation Procedure (PGP) traces and computing evaluation metrics.
 *
 * This tool processes PGP experiment data by:
 * - Parsing MAS (Multi-Agent System) log files to extract agent and PGP-specific logs
 * - Writing to disk the conversation history and generated plans for each PGP invocation
 * - Computing evaluation metrics when requested
 * - Retrieving generation data when requested, including latency, token consumption and cost
 * - Exporting results to CSV format for further analysis
 */
class AnalyzePGP : CliktCommand() {
    val expDir: String by option()
        .default("experiments/")
        .help("The directory where there are the PGP traces to analyze.")

    val computeMetrics: Boolean by option()
        .flag()
        .help("Whether to compute metrics or not.")

    val retrieveGenerationData: Boolean by option()
        .flag()
        .help("Whether to retrieve generation data from OpenRouter or not.")

    val metricsDir: String by option()
        .default("metrics/")
        .help("Whether to store the computed metrics for a given PGP.")

    val authToken: String by option(envvar = "API_KEY")
        .default(DEFAULT_TOKEN)
        .help("The secret API key to use for authentication with the server")

    init {
        ModuleLoader.loadModules()
    }

    override fun run() {
        val masLogFile = findMasLogFile(expDir) ?: return
        extractAgentLogFiles(expDir, masLogFile).forEach { agentLogFile ->
            val context = InvocationContext.from(masLogFile, agentLogFile)
            extractPgpLogFiles(expDir, agentLogFile).forEach { pgpLogFile ->
                val pgpId = extractLastId(pgpLogFile.name)
                val pgpName = extractLastComponent(pgpLogFile.name)
                pgpId?.let {
                    val pgpInvocation = LMPGPInvocation.from(pgpId, agentLogFile, pgpLogFile)

                    writeChatHistory(pgpInvocation.history, "chat_history_$pgpName")
                    writeGenerationResult(pgpInvocation, "generation_result_$pgpName")

                    if (computeMetrics) {
                        computeMetrics(context, pgpInvocation, "pgp_eval_$pgpName")
                    }
                }
            }
        }
    }

    fun writeGenerationResult(
        generationResult: LMPGPInvocation,
        fileName: String,
        fileExtension: String = ".txt",
    ) {
        // Since they are single-turn chats, it takes only the first response
        val rawContent = generationResult.rawMessageContents.firstOrNull()
        val metricsDirectory = File(metricsDir).apply { mkdirs() }
        val file = File(metricsDirectory, fileName + fileExtension)
        if (!file.exists() && rawContent != null) {
            writeToFile(rawContent, file, "Generated plans, admissible beliefs and goals")
        }
    }

    fun writeChatHistory(
        history: List<ChatMessage>,
        fileName: String,
        fileExtension: String = ".txt",
    ) {
        val metricsDirectory = File(metricsDir).apply { mkdirs() }
        val file = File(metricsDirectory, fileName + fileExtension)
        if (!file.exists() && history.isNotEmpty()) {
            writeToFile(formatHistory(history), file, "History")
        }
    }

    private fun formatHistory(history: List<ChatMessage>) =
        history
            .joinToString("\n") {
                "-".repeat(80) + "\n" +
                    it.role.role.capitalize() + "\n" +
                    "-".repeat(80) + "\n" +
                    it.content + "\n"
            }

    private fun computeMetrics(
        context: InvocationContext,
        pgpInvocation: LMPGPInvocation,
        fileName: String,
        fileExtension: String = ".json",
    ) {
        val metricsComputer = MetricsComputer(authToken, retrieveGenerationData)
        val evaluationResults = metricsComputer.eval(context, pgpInvocation)
        val metricsDirectory = File(metricsDir).apply { mkdirs() }
        val evalFile = File(metricsDirectory, fileName + fileExtension)

        evalFile.writeText(json.encodeToString(evaluationResults))
        println("\nResults written to: ${evalFile.absolutePath}")
    }
}

fun main(args: Array<String>) =
    AnalyzePGP()
        .context { terminal = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, interactive = true) }
        .main(args)
