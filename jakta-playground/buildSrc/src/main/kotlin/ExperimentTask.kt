import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import java.io.File
import java.util.concurrent.TimeUnit

abstract class ExperimentTask : DefaultTask() {
    @get:Input
    @get:Option(option = "model", description = "Model identifier")
    abstract val model: Property<String>

    @get:Input
    @get:Option(option = "repetitions", description = "Number of repetitions")
    abstract val repetitions: Property<String>

    @get:Input
    @get:Option(option = "timeoutSeconds", description = "Timeout in seconds")
    abstract val timeoutSeconds: Property<String>

    @get:Input
    @get:Option(option = "provider", description = "Provider URL")
    abstract val provider: Property<String>

    @get:Input
    @get:Option(option = "temperature", description = "Temperature parameter")
    abstract val temperature: Property<String>

    @get:Input
    @get:Option(option = "maxTokens", description = "Maximum tokens parameter")
    abstract val maxTokens: Property<String>

    init {
        model.convention("openai/gpt-4.1")
        repetitions.convention("5")
        timeoutSeconds.convention("60")
        provider.convention("https://openrouter.ai/api/v1/")
        temperature.convention("0.5")
        maxTokens.convention("4096")
    }

    @TaskAction
    fun runExperiment() {
        val repetitions = try {
            repetitions.get().toIntOrNull() ?: throw IllegalArgumentException("Invalid repetitions value")
        } catch (_: NumberFormatException) {
            throw GradleException("Repetitions must be a valid integer")
        }

        val timeout = try {
            timeoutSeconds.get().toLongOrNull() ?: throw IllegalArgumentException("Invalid timeout value")
        } catch (_: NumberFormatException) {
            throw GradleException("Timeout must be a valid long")
        }

        for (i in 1..repetitions) {
            logger.lifecycle("Running experiment with model ${model.get()} (run $i/${repetitions})")
            logger.lifecycle("Timeout: ${timeoutSeconds.get()}")

            // Get the absolute path to the Gradle wrapper script
            val rootDir = project.rootProject.rootDir
            val gradleCommand = if (System.getProperty("os.name").lowercase().contains("windows")) {
                rootDir.resolve("gradlew.bat").absolutePath
            } else {
                rootDir.resolve("gradlew").absolutePath
            }

            val gradleWrapperFile = File(gradleCommand)
            if (!gradleWrapperFile.exists()) {
                throw GradleException("Cannot find Gradle wrapper at: $gradleCommand")
            }

            // Make sure the Gradle wrapper is executable (Unix systems)
            if (!System.getProperty("os.name").lowercase().contains("windows")) {
                gradleWrapperFile.setExecutable(true)
            }

            logger.lifecycle("Using Gradle wrapper at: $gradleCommand")

            val processBuilder = ProcessBuilder(
                gradleCommand,
                "runExperiment",
                "--model=${model.get()}",
                "--repetitions=${repetitions}",
                "--timeoutSeconds=${timeout}",
                "--provider=${provider.get()}",
                "--temperature=${temperature.get()}",
                "--maxTokens=${maxTokens.get()}"
            )

            processBuilder.directory(project.projectDir)
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT)
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT)

            val process = processBuilder.start()
            val completed = process.waitFor(timeout, TimeUnit.SECONDS)

            if (!completed) {
                process.destroyForcibly()
                logger.warn("Timeout reached for run $i")
            } else if (process.exitValue() != 0) {
                logger.error("Error occurred during run $i with exit code ${process.exitValue()}")
                throw GradleException("Experiment failed with exit code ${process.exitValue()}")
            }
        }

        logger.lifecycle("All experiment runs completed for model ${model.get()}")
    }
}
