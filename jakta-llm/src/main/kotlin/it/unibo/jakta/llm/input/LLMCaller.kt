package it.unibo.jakta.llm.input

import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.gson.gson
import it.unibo.jakta.llm.output.ChatMessage
import org.openapitools.client.api.DefaultApi
import org.openapitools.client.models.AskForPlanGenerationRequest

class LLMCaller {
    suspend fun callLLM(prompt: String): ChatMessage? {
        val request = AskForPlanGenerationRequest(prompt)
        val response = apiClient.askForPlanGeneration(request)
        return when (response.status) {
            HttpStatusCode.Companion.OK.value -> {
                val step = response.body()
                ChatMessage(step.thought, step.code)
            }
            else -> {
                println(response.body())
                return null
            }
        }
    }

    companion object {
        const val SERVER_URL = "http://0.0.0.0:2024"
        const val REQUEST_TIMEOUT_MILLIS: Long = 120_000
        const val CONNECT_TIMEOUT_MILLIS: Long = 10_000
        const val SOCKET_TIMEOUT_MILLIS: Long = 120_000

        val apiClient =
            DefaultApi(
                baseUrl = SERVER_URL,
                httpClientEngine = CIO.create(),
                httpClientConfig = { config ->
                    config.install(HttpTimeout) {
                        requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
                        connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
                        socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
                    }
                    config.install(ContentNegotiation) {
                        gson()
                    }
                },
            )
    }
}
