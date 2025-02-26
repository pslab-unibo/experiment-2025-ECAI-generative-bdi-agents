package it.unibo.jakta.generationstrategies.lm

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole

sealed interface GenerationResult {
    val msg: ChatMessage
}

data class Expression(override val msg: ChatMessage, val fragmentToParse: String) : GenerationResult {
    constructor(content: String, fragmentToParse: String) :
        this(ChatMessage(ChatRole.Assistant, content), fragmentToParse)
}

data class Precondition(override val msg: ChatMessage, val fragmentToParse: String) : GenerationResult {
    constructor(content: String, fragmentToParse: String) :
        this(ChatMessage(ChatRole.Assistant, content), fragmentToParse)
}

data class Goal(override val msg: ChatMessage, val fragmentToParse: String) : GenerationResult {
    constructor(content: String, fragmentToParse: String) :
        this(ChatMessage(ChatRole.Assistant, content), fragmentToParse)
}

data class Stop(override val msg: ChatMessage) : GenerationResult {
    constructor(content: String) : this(ChatMessage(ChatRole.Assistant, content))
}

data class Failure(override val msg: ChatMessage) : GenerationResult {
    constructor(content: String) : this(ChatMessage(ChatRole.Assistant, content))
}
