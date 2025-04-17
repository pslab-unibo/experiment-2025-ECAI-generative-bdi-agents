package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

interface Parser {
    fun parse(input: Char)

    fun parse(input: String)

    fun reset()
}
