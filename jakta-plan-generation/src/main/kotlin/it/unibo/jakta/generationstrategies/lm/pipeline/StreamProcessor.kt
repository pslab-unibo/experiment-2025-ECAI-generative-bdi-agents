package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class StreamProcessor(val api: OpenAI) {
    suspend fun getChatCompletionResult(logger: KLogger?, request: ChatCompletionRequest): ParsingResult {
        val deferredResult = CompletableDeferred<ParsingResult>()
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                var openBrackets = 0
                var isComplete = false
                val outputBuffer = StringBuilder()
                val expressionBuffer = StringBuilder()

                api.chatCompletions(request)
                    .collect { response ->
                        val content = response.choices.first().delta?.content.orEmpty()
//                        print(content)
                        outputBuffer.append(content)
                        for (char in content) {
                            when (char) {
                                '[' -> {
                                    if (!isComplete) {
                                        isComplete = true
                                        expressionBuffer.append(char)
                                    }
                                    openBrackets++
                                }

                                ']' -> {
                                    if (openBrackets > 0) {
                                        openBrackets--
                                        if (openBrackets == 0) {
                                            expressionBuffer.append(char)
                                            val result = expressionBuffer.toString()
                                            isComplete = false
                                            deferredResult.complete(ParserSuccess(outputBuffer.toString(), result))
                                            cancel()
                                        }
                                    }
                                }
                                else -> {
                                    if (isComplete) {
                                        expressionBuffer.append(char)
                                    }
                                }
                            }
                        }
                    }

                deferredResult.complete(ParserStop(outputBuffer.toString()))
            } catch (_: CancellationException) {
                logger?.trace { "\nFlow was cancelled." }
                deferredResult.complete(ParserFail("Cancelled early"))
            } catch (e: Exception) {
                logger?.error { "\nFlow threw an unexpected exception: ${e.message}" }
                deferredResult.complete(ParserFail("Error: ${e.message}"))
            }
        }

        job.join()
        return deferredResult.await()
    }
}
