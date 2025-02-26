package it.unibo.jakta.playground

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_LM_SERVER_URL
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_MAX_TOKENS
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_TEMPERATURE
import it.unibo.jakta.generationstrategies.lm.DefaultPlanGeneratorConfig.DEFAULT_TOKEN
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.playground.BlocksWorldLiterate.gripperOperator
import kotlinx.coroutines.runBlocking
import kotlin.text.matches

class ExperimentRunner : CliktCommand() {
    private val nameGenerator = NameGenerator()
    private val expRunnerLogger = logger("ExperimentRunner")
    private val urlRegex = "^https?://([\\w.-]+)(:\\d+)?(/.*)?$".toRegex()

    val logServerURL: String by option()
        .default(DEFAULT_LOG_SERVER_URL)
        .help("Number of greetings")
        .check("value must be a valid URL") { it.matches(urlRegex) }

    val logToFile: Boolean by option()
        .flag()
        .help("Whether to output log to the local filesystem.")

    val logToConsole: Boolean by option()
        .flag(default = true)
        .help("Whether to output log to std output.")

    val logLevel: LogbackLogLevel by option()
        .enum<LogbackLogLevel>()
        .default(DEFAULT_LOG_LEVEL)
        .help("The minimum log level")

    val logDir: String by option()
        .default("logs")
        .help("Whether to output log to the local filesystem.")

    val lmServerUrl: String by option()
        .default(DEFAULT_LM_SERVER_URL)
        .help("Url of the server with an OpenAI-compliant API.")
        .check("value must be a valid URL") { it.matches(urlRegex) }

    val lmServerToken: String by option()
        .default(DEFAULT_TOKEN)
        .help("The secret API key to use for authentication with the server")

    val modelId: String by option()
        .help("ID of the model to use.")
        .required()

    val maxTokensParam: Int by option()
        .int()
        .default(DEFAULT_MAX_TOKENS)
        .help(
            """
            The maximum number of tokens allowed for the generated answer. 
            By default, the number of tokens the model can return will 
            be ($DEFAULT_MAX_TOKENS - prompt tokens).
            """.trimIndent(),
        )

    val temperatureParam: Double by option()
        .double()
        .default(DEFAULT_TEMPERATURE)
        .help(
            """
            What sampling temperature to use, between $MIN_TEMPERATURE and $MAX_TEMPERATURE. 
            Higher values like 0.8 will make the output more random, 
            while lower values like 0.2 will make it more focused and deterministic.
            """.trimIndent(),
        )
        .check("value must be positive number") {
            it >= MIN_TEMPERATURE && it <= MAX_TEMPERATURE
        }

    override fun run() =
        runBlocking {
            expRunnerLogger.info { "Experiment started" }
            val expName = nameGenerator.randomName()

            val logConfig = LoggingConfig(
                logServerURL = logServerURL,
                logToFile = logToFile,
                logToConsole = logToConsole,
                logLevel = logLevel.level,
                logDir = "$logDir/$expName",
            )
            expRunnerLogger.info { logConfig }
            val genStrat = LMGenerationStrategy.react {
                url = lmServerUrl
                token = lmServerToken
                model = modelId
                maxTokens = maxTokensParam
                temperature = temperatureParam
            }
            expRunnerLogger.info { genStrat }
            mas {
                loggingConfig = logConfig
                generationStrategy = genStrat
                gripperOperator()
            }.start()

            expRunnerLogger.info { "Experiment terminated" }
            expRunnerLogger.info { "Results logged to $expName" }
        }
    companion object {
        val DEFAULT_LOG_LEVEL = LogbackLogLevel.DEBUG
        const val MIN_TEMPERATURE = 0.0
        const val MAX_TEMPERATURE = 2.0
        const val DEFAULT_LOG_SERVER_URL = "http://localhost:8081"
    }
}

fun main(args: Array<String>): Unit = ExperimentRunner()
    .context { terminal = Terminal(ansiLevel = AnsiLevel.TRUECOLOR, interactive = true) }
    .main(args)
