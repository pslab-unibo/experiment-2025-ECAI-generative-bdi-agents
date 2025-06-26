package it.unibo.jakta.playground.evaluation.scripts

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
import it.unibo.jakta.playground.evaluation.InvocationContext
import it.unibo.jakta.playground.evaluation.LMPGPInvocation
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractAgentLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.extractPgpLogFiles
import it.unibo.jakta.playground.evaluation.LogFileUtils.findMasLogFile
import it.unibo.jakta.playground.evaluation.MetricsComputer
import it.unibo.jakta.playground.evaluation.PGPEvaluationResult
import java.io.File

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
                    printHistory(pgpInvocation.history)
                    printGeneratedPlans(pgpInvocation.generatedPlans)

                    if (computeMetrics) {
                        computeMetrics(context, pgpInvocation)
                    }
                }
            }
        }
    }

    private fun printGeneratedPlans(generatedPlans: List<Plan>) {
        "-".repeat(80).let(::println)
        println("Generated Plans")
        "-".repeat(80).let(::println)

        if (generatedPlans.isEmpty()) {
            println("No generated plans")
        } else {
            generatedPlans
                .mapNotNull { planFormatter.format(it) }
                .joinToString("\n\n") { it.dropNumbers() }
                .let(::println)
        }
    }

    private fun printHistory(history: List<ChatMessage>) {
        history
            .joinToString("\n") {
                "-".repeat(80) + "\n" +
                    it.role.role.capitalize() + "\n" +
                    "-".repeat(80) + "\n" +
                    it.content + "\n"
            }.let(::println)
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
    ) {
        val evaluationResults = MetricsComputer().eval(context, pgpInvocation)
        printEvalResult(evaluationResults)

        metricsDir.let {
            val metricsDirectory = File(it).apply { mkdirs() }

            val csvFile = File(metricsDirectory, "pgp_evaluation_result.csv")
            csvFile.outputStream().use { outputStream ->
                PGPEvaluationResult.writeCsv(evaluationResults, outputStream)
            }
            println("\nResults written to: ${csvFile.absolutePath}")
        }
    }
}

fun main(args: Array<String>) =
    AnalyzePGP()
        .context { terminal = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, interactive = true) }
        .main(args)
