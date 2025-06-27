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
import it.unibo.jakta.agents.bdi.engine.Jakta.dropNumbers
import it.unibo.jakta.agents.bdi.engine.formatters.DefaultFormatters.planFormatter
import it.unibo.jakta.agents.bdi.engine.logging.loggers.JaktaLogger.Companion.extractLastId
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.playground.ModuleLoader
import it.unibo.jakta.playground.evaluation.FileProcessor.writeToFile
import it.unibo.jakta.playground.evaluation.InvocationContext
import it.unibo.jakta.playground.evaluation.LMPGPInvocation
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractAgentLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractPgpLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.findMasLogFile
import it.unibo.jakta.playground.evaluation.MetricsComputer
import it.unibo.jakta.playground.evaluation.PGPEvaluationResult
import java.io.File

/**
 * Command-line tool for analyzing Plan Generation Procedure (PGP) traces and computing evaluation metrics.
 *
 * This tool processes PGP experiment data by:
 * - Parsing MAS (Multi-Agent System) log files to extract agent and PGP-specific logs
 * - Writing to disk the conversation history and generated plans for each PGP invocation
 * - Computing evaluation metrics when requested
 * - Exporting results to CSV format for further analysis
 */
class AnalyzePGP : CliktCommand() {
    val expDir: String by option()
        .default("experiments/")
        .help("The directory where there are the PGP traces to analyze.")

    val computeMetrics: Boolean by option()
        .flag()
        .help("Whether to compute metrics or not")

    val metricsDir: String by option()
        .default("metrics/")
        .help("Whether to store the computed metrics for a given PGP.")

    init {
        ModuleLoader.loadModules()
    }

    override fun run() {
        val masLogFile = findMasLogFile(expDir) ?: return
        extractAgentLogFiles(expDir, masLogFile).forEach { agentLogFile ->
            val context = InvocationContext.from(masLogFile, agentLogFile)
            extractPgpLogFiles(expDir, agentLogFile).forEach { pgpLogFile ->
                val pgpId = extractLastId(pgpLogFile.name)
                pgpId?.let {
                    val pgpInvocation = LMPGPInvocation.from(pgpId, agentLogFile, pgpLogFile)

                    writeChatHistory(pgpInvocation.history)
                    writeGeneratedPlans(pgpInvocation.generatedPlans)

                    if (computeMetrics) {
                        computeMetrics(context, pgpInvocation)
                    }
                }
            }
        }
    }

    private fun getGeneratedPlans(generatedPlans: List<Plan>) =
        buildString {
            appendLine("-".repeat(80))
            appendLine("Generated Plans")
            appendLine("-".repeat(80))

            if (generatedPlans.isEmpty()) {
                appendLine("No generated plans")
            } else {
                val formattedPlans =
                    generatedPlans
                        .mapNotNull { planFormatter.format(it) }
                        .joinToString("\n\n") { it.dropNumbers() }
                append(formattedPlans)
            }
        }

    fun writeGeneratedPlans(
        generatedPlans: List<Plan>,
        filename: String = "generated_plans.txt",
    ) {
        val metricsDirectory = File(metricsDir).apply { mkdirs() }
        val file = File(metricsDirectory, filename)
        writeToFile(getGeneratedPlans(generatedPlans), file, "Generated plans")
    }

    fun writeChatHistory(
        history: List<ChatMessage>,
        filename: String = "chat_history.txt",
    ) {
        val metricsDirectory = File(metricsDir).apply { mkdirs() }
        val file = File(metricsDirectory, filename)
        writeToFile(getHistory(history), file, "History")
    }

    private fun getHistory(history: List<ChatMessage>) =
        history
            .joinToString("\n") {
                "-".repeat(80) + "\n" +
                    it.role.role.capitalize() + "\n" +
                    "-".repeat(80) + "\n" +
                    it.content + "\n"
            }

    private fun printEvalResult(evaluationResults: List<PGPEvaluationResult>) {
        "-".repeat(80).let(::println)
        println("PGP evaluation result")
        "-".repeat(80).let(::println)
        evaluationResults.joinToString("\n").let(::println)
    }

    private fun computeMetrics(
        context: InvocationContext,
        pgpInvocation: LMPGPInvocation,
        filename: String = "pgp_evaluation_result.csv",
    ) {
        val evaluationResults = MetricsComputer().eval(context, pgpInvocation)
        printEvalResult(evaluationResults)

        val metricsDirectory = File(metricsDir).apply { mkdirs() }
        val csvFile = File(metricsDirectory, filename)
        csvFile.outputStream().use { outputStream ->
            PGPEvaluationResult.writeCsv(evaluationResults, outputStream)
        }
        println("\nResults written to: ${csvFile.absolutePath}")
    }
}

fun main(args: Array<String>) =
    AnalyzePGP()
        .context { terminal = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, interactive = true) }
        .main(args)
