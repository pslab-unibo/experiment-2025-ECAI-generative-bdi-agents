package it.unibo.jakta.llm.tools

data class ToolMetadata(
    val name: String,
    val description: String,
    val input: Map<String, String>,
    val output: Map<String, String>,
) {
    override fun toString(): String {
        val inputParams = input.entries.joinToString(", ") { (key, value) -> "$key: $value" }
        val description = description
        val functionName = name
        val outputType = output.values.firstOrNull() ?: "Unit"

        return """
            |/**
            | * $description
            | */
            |$functionName: ($inputParams) -> $outputType
            """.trimMargin("|")
    }
}
