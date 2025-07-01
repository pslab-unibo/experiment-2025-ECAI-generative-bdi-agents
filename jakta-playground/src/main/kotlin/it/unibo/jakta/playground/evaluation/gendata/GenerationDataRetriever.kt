package it.unibo.jakta.playground.evaluation.gendata

import io.kotest.common.runBlocking

class GenerationDataRetriever(
    authToken: String,
) {
    private val client = OpenRouterClient(authToken)

    fun retrieve(chatCompletionId: String) =
        runBlocking {
            client.use { client ->
                client.getGenerationData(chatCompletionId)
            }
        }
}
