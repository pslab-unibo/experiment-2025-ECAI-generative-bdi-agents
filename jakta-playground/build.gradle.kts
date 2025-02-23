import org.gradle.kotlin.dsl.register
import java.io.OutputStream

plugins {
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinx)
}

dependencies {
    api(project(":jakta-dsl"))
    api(project(":jakta-plan-generation"))

    implementation(libs.bundles.kotlin.testing)
    implementation(libs.kotlin.coroutines)
    implementation(libs.openai)

    implementation("io.kotest:kotest-framework-datatest:5.9.1")
    implementation("com.github.ajalt.clikt:clikt:5.0.1")
    implementation("com.github.ajalt.clikt:clikt-markdown:5.0.1")
}

tasks.register<JavaExec>("runExperiment") {
    description = "Run a MAS with the given experimental config."
    group = "application"

    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "${project.group}.playground.experimentrunner.ExperimentRunnerKt"
}

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

val globalPython = findExecutablePath("python3", "python") { path ->
    exec {
        errorOutput = OutputStream.nullOutputStream()
        standardOutput = OutputStream.nullOutputStream()
        commandLine(path, "--version")
    }.exitValue == 0
}?.absolutePath

val localPythonEnvRoot = projectDir.resolve("build").resolve("python")

val localPython
    get() = fileTree(localPythonEnvRoot) {
        include("**/python")
        include("**/python.exe")
    }.firstOrNull()?.absolutePath

val python
    get() = localPython ?: globalPython ?: error("Python executable not found")

tasks.register<Exec>("createVenv") {
    description = "Create a virtual environment."
    group = "build"

    outputs.dir(localPythonEnvRoot)
    workingDir(projectDir)
    commandLine(python, "-m", "venv", localPythonEnvRoot.path)

    doLast {
        when (val path = localPython) {
            null -> error("Virtual environment creation failed")
            else -> println("Created local Python environment in $path")
        }
    }
}

fun projectArgs(): List<String> =
    (project.findProperty("args") as? String)
        ?.split("\\s+".toRegex())
        ?: listOf("--version")

tasks.register<Exec>("python") {
    description = "Run a python command."
    group = "application"

    workingDir(projectDir)
    commandLine(listOf(python, "-m") + projectArgs())
}

tasks.register<Exec>("dvc") {
    description = "Run a dvc command."
    group = "application"

    workingDir(projectDir)
    val dvcExecutable = "$localPythonEnvRoot/bin/dvc"
    commandLine(listOf(dvcExecutable) + projectArgs())
}
