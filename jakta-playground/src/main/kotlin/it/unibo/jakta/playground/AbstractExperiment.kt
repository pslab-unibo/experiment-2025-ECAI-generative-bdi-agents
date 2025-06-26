package it.unibo.jakta.playground

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.double
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.LoggerFactory
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_LM_SERVER_URL
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_MAX_TOKENS
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_TEMPERATURE
import it.unibo.jakta.agents.bdi.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_TOKEN
import org.apache.logging.log4j.Logger
import java.util.UUID
import kotlin.system.exitProcess
import kotlin.text.matches
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

abstract class AbstractExperiment : CliktCommand() {
    private val delegate = LoggerFactory.create("ExperimentRunner", "", LoggingConfig())
    private val expRunnerLogger: Logger get() = delegate.logger
    private val urlRegex = "^https?://([\\w.-]+)(:\\d+)?(/.*)?$".toRegex()

    val logToFile: Boolean by option()
        .flag()
        .help("Whether to output logs to the local filesystem.")

    val logToConsole: Boolean by option()
        .flag(default = true)
        .help("Whether to output log to std output.")

    val logToServer: Boolean by option()
        .flag()
        .help("Whether to output logs to a log server.")

    val logLevel: Log4jLevel by option()
        .enum<Log4jLevel>()
        .default(DEFAULT_LOG_LEVEL)
        .help("The minimum log level")

    val logDir: String by option()
        .default(DEFAULT_LOG_DIR)
        .help("Whether to output log to the local filesystem.")

    val logServerUrl: String by option()
        .default(DEFAULT_LM_SERVER_URL)
        .help("Url of the server where logs are sent.")
        .check("value must be a valid URL") { it.matches(urlRegex) }

    val lmServerUrl: String by option()
        .default(DEFAULT_LM_SERVER_URL)
        .help("Url of the server with an OpenAI-compliant API.")
        .check("value must be a valid URL") { it.matches(urlRegex) }

    val lmServerToken: String by option(envvar = "API_KEY")
        .default(DEFAULT_TOKEN)
        .help("The secret API key to use for authentication with the server")

    val modelId: String by option()
        .help("ID of the model to use.")
        .required()

    val maxTokens: Int by option()
        .int()
        .default(DEFAULT_MAX_TOKENS)
        .help(
            """
            The maximum number of tokens allowed for the generated answer. 
            By default, the number of tokens the model can return will 
            be $DEFAULT_MAX_TOKENS.
            """.trimIndent(),
        )

    val temperature: Double by option()
        .double()
        .default(DEFAULT_TEMPERATURE)
        .help(
            """
            What sampling temperature to use, between $MIN_TEMPERATURE and $MAX_TEMPERATURE. 
            Higher values like 0.8 will make the output more random, 
            while lower values like 0.2 will make it more focused and deterministic.
            """.trimIndent(),
        ).check("value must be positive number, between $MIN_TEMPERATURE and $MAX_TEMPERATURE") {
            it >= MIN_TEMPERATURE && it <= MAX_TEMPERATURE
        }

    val timeoutMillis: Long by option()
        .long()
        .default(DEFAULT_TIMEOUT)
        .help("Time in milliseconds before the MAS shutdown.")

    val runId: String? by option()
        .help("The UUID that identifies the experimental run.")

    override fun run() {
        expRunnerLogger.info("Experiment started")
//        expRunnerLogger.info("Using API_KEY: $lmServerToken")
        val runId = runId ?: UUID.randomUUID().toString()

        val logConfig = createLoggingConfig(runId)
        expRunnerLogger.info(logConfig)

        val genStrat = createGenerationStrategy()
//        expRunnerLogger.info(genStrat)

        val mas = createMas(logConfig, genStrat)

        expRunnerLogger.info("Shutting down in $timeoutMillis milliseconds")
        mas.start()

        Thread.sleep(timeoutMillis)

        mas.stop()

        expRunnerLogger.info("Run id: $runId")
        exitProcess(0)
    }

    abstract fun createMas(
        logConfig: LoggingConfig,
        genStrat: GenerationStrategy?,
    ): Mas

    abstract fun createLoggingConfig(expName: String): LoggingConfig

    abstract fun createGenerationStrategy(): GenerationStrategy?

    companion object {
        val DEFAULT_TIMEOUT = 60.seconds.toLong(DurationUnit.MILLISECONDS)
        const val DEFAULT_LOG_DIR = "logs"
        val DEFAULT_LOG_LEVEL = Log4jLevel.INFO
        const val MIN_TEMPERATURE = 0.0
        const val MAX_TEMPERATURE = 1.0
    }
}
