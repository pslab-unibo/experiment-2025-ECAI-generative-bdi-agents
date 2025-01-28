package it.unibo.jakta.llm.tools

class StopTool : Tool {
    override val metadata =
        ToolMetadata(
            name = "execute(\"stop\")",
            description = "Stops the agent, preventing the execution of subsequent actions.",
            input = emptyMap(),
            output = emptyMap(),
        )
}
