package it.unibo.jakta.generationstrategies.lm.pipeline.filtering

interface ContextFilter {
    fun filter(extendedContext: ExtendedAgentContext): ExtendedAgentContext
}
