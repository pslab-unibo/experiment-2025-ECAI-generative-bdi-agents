package it.unibo.jakta.playground.experiment

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
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import it.unibo.jakta.agents.bdi.Mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_LM_SERVER_URL
import it.unibo.jakta.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_MAX_TOKENS
import it.unibo.jakta.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_TEMPERATURE
import it.unibo.jakta.generationstrategies.lm.DefaultGenerationConfig.DEFAULT_TOKEN
import kotlin.text.matches

abstract class Experiment : CliktCommand() {
    private val nameGenerator = NameGenerator()
    private val expRunnerLogger = logger("ExperimentRunner")
    private val urlRegex = "^https?://([\\w.-]+)(:\\d+)?(/.*)?$".toRegex()

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
        .default(DEFAULT_LOG_DIR)
        .help("Whether to output log to the local filesystem.")

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
            be ($DEFAULT_MAX_TOKENS - prompt tokens).
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
        )
        .check("value must be positive number, between $MIN_TEMPERATURE and $MAX_TEMPERATURE") {
            it >= MIN_TEMPERATURE && it <= MAX_TEMPERATURE
        }

    override fun run() {
        expRunnerLogger.info { "Experiment started" }
        val expName = nameGenerator.randomName()

        val logConfig = createLoggingConfig(expName)
        expRunnerLogger.info { logConfig }

        val genStrat = createGenerationStrategy()
//        expRunnerLogger.info { genStrat }

        createMas(logConfig, genStrat).start()

        expRunnerLogger.info { "Results logging to $expName" }
    }

    abstract fun createMas(logConfig: LoggingConfig, genStrat: GenerationStrategy?): Mas

    abstract fun createLoggingConfig(expName: String): LoggingConfig

    abstract fun createGenerationStrategy(): GenerationStrategy?

    companion object {
        const val DEFAULT_LOG_DIR = "logs"
        val DEFAULT_LOG_LEVEL = LogbackLogLevel.INFO
        const val MIN_TEMPERATURE = 0.0
        const val MAX_TEMPERATURE = 2.0
    }
}
