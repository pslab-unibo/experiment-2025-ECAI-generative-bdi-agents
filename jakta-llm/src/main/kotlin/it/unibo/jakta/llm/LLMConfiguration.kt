package it.unibo.jakta.llm

import it.unibo.jakta.llm.tools.Tool

data class LLMConfiguration(
    val taskName: String,
    val goalName: String,
    val goalDescription: String,
    val planPaths: PlanPaths,
    val tools: List<Tool>,
    val observations: String,
)

data class PlanPaths(
    val compilationDirectory: String = COMPILATION_DIR,
    val moduleDirectory: String = MODULE_DIR,
    val sourceDirectory: String = SOURCE_DIR,
    val packageDirectory: String = PACKAGE_NAME,
) {
    val packageName = packageDirectory.replace('/', '.')
    val sourceWorkingDirectory = "$MODULE_DIR/$SOURCE_DIR/$PACKAGE_NAME"
    val compilationWorkingDirectory = "$MODULE_DIR/$COMPILATION_DIR"

    companion object {
        const val COMPILATION_DIR = "build/classes/kotlin/main"
        const val MODULE_DIR = "jakta-playground"
        const val SOURCE_DIR = "src/main/kotlin"
        const val PACKAGE_NAME = "it/unibo/jakta/playground"
    }
}
