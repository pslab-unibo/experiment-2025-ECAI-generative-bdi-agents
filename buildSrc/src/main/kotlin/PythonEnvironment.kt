import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.register
import java.io.File
import java.io.OutputStream

/**
 * Utility class for managing Python environment in Gradle projects.
 */
class PythonEnvironment(private val project: Project) {

    /**
     * Finds an executable in the system PATH.
     */
    fun findExecutablePath(
        name: String,
        vararg otherNames: String,
        test: (File) -> Boolean = { true },
    ): File? {
        val names = listOf(name, *otherNames).flatMap { listOf(it, "$it.exe") }
        return System.getenv("PATH")
            .split(File.pathSeparatorChar)
            .asSequence()
            .map { File(it) }
            .flatMap { path -> names.asSequence().map { path.resolve(it) } }
            .filter { it.exists() }
            .filter(test)
            .firstOrNull()
    }

    /**
     * Path to the global Python executable.
     */
    val globalPython by lazy {
        findExecutablePath("python3", "python") { path ->
            project.exec {
                errorOutput = OutputStream.nullOutputStream()
                standardOutput = OutputStream.nullOutputStream()
                commandLine(path, "--version")
                isIgnoreExitValue = true
            }.exitValue == 0
        }?.absolutePath
    }

    /**
     * Path to the local Python virtual environment root.
     */
    val localPythonEnvRoot by lazy {
        project.projectDir.resolve("build").resolve("python")
    }

    /**
     * Path to the local Python executable in the virtual environment.
     */
    val localPython get() = project.fileTree(localPythonEnvRoot) {
        include("**/python")
        include("**/python.exe")
    }.firstOrNull()?.absolutePath

    /**
     * Path to the Python executable to use (local if available, global otherwise).
     */
    val python get() = localPython ?: globalPython ?: throw GradleException("Python executable not found")

    /**
     * Path to the DVC executable.
     */
    val dvcExecutable get() = if (System.getProperty("os.name").lowercase().contains("windows")) {
        "$localPythonEnvRoot/Scripts/dvc.exe"
    } else {
        "$localPythonEnvRoot/bin/dvc"
    }

    fun projectArgs(): List<String> =
        (project.findProperty("args") as? String)
            ?.split("\\s+".toRegex())
            ?: listOf("--version")

    fun registerTasks() {
        project.tasks.register<Exec>("createVenv") {
            description = "Create a Python virtual environment."
            group = "build"

            outputs.dir(localPythonEnvRoot)
            workingDir(project.projectDir)
            commandLine(globalPython ?: throw GradleException("Global Python not found"), "-m", "venv", localPythonEnvRoot.path)

            doLast {
                when (val path = localPython) {
                    null -> throw GradleException("Virtual environment creation failed")
                    else -> println("Created local Python environment in $path")
                }
            }
        }

        project.tasks.register<Exec>("installRequirements") {
            description = "Install requirements in the virtual environment."
            group = "build"

            workingDir(project.projectDir)
            val pipExecutable = if (System.getProperty("os.name").lowercase().contains("windows")) {
                "$localPythonEnvRoot/Scripts/pip"
            } else {
                "$localPythonEnvRoot/bin/pip"
            }
            commandLine(pipExecutable, "install", "-r", "requirements.txt")
        }

        project.tasks.register<Exec>("python") {
            description = "Run a Python command."
            group = "application"

            workingDir(project.projectDir)
            commandLine(listOf(python) + projectArgs())
        }

        project.tasks.register<Exec>("dvc") {
            description = "Run a DVC command."
            group = "application"

            workingDir(project.projectDir)
            commandLine(listOf(dvcExecutable) + projectArgs())
        }
    }
}