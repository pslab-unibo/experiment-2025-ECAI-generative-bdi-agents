package it.unibo.jakta.llm.tools

class PrintTool : Tool {
    override val metadata =
        ToolMetadata(
            name = "Print",
            description = "Prints a message to the console and a newline.",
            input = mapOf("message" to "String"),
            output = emptyMap(),
        )
}
