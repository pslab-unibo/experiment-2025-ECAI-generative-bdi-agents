package it.unibo.jakta.llm.output

data class ChatMessage(
    val thought: String,
    val action: String,
)
