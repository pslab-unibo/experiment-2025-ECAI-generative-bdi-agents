package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering

interface ContextFilter {
    fun filter(extendedContext: ExtendedAgentContext): ExtendedAgentContext
}
