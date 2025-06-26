package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result

interface RequestFailure : RequestResult {
    data class NetworkRequestFailure(
        override val rawContent: String,
    ) : RequestFailure
}
