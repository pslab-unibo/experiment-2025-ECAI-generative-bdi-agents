package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result

sealed interface RequestFailure : RequestResult {
    data class NetworkRequestFailure(
        val rawContent: String,
    ) : RequestFailure
}
