package it.unibo.jakta.generationstrategies.lm

sealed interface FinishResult {
    val value: String
}

@JvmInline
value class Expression(override val value: String) : FinishResult

@JvmInline
value class ToolCall(override val value: String) : FinishResult

@JvmInline
value class Stop(override val value: String) : FinishResult
