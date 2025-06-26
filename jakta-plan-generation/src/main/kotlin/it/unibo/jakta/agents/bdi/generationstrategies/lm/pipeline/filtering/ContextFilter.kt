package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering

fun interface ContextFilter {
    fun filter(extendedContext: ExtendedAgentContext): ExtendedAgentContext

    companion object {
        fun List<ContextFilter>.applyAllTo(context: ExtendedAgentContext): ExtendedAgentContext =
            this.fold(context) { currentContext, filter ->
                filter.filter(currentContext)
            }
    }
}
