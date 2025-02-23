package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.Expression
import it.unibo.jakta.generationstrategies.lm.FinishResult
import it.unibo.jakta.generationstrategies.lm.Stop
import it.unibo.jakta.generationstrategies.lm.ToolCall
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.ConnectException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.text.iterator

class RequestGeneratorImpl(val api: OpenAI) : RequestGenerator {
    override suspend fun requestTextCompletion(logger: KLogger?, request: ChatCompletionRequest): FinishResult {
        val deferredResult = CompletableDeferred<FinishResult>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var openBrackets = 0
                val output = StringBuilder()
                val expression = StringBuilder()
                var capturing = false
                var isAtSymbolDetected = false

                api.chatCompletions(request)
                    .collect { response ->
                        val content = response.choices.first().delta?.content.orEmpty()
                        logger?.info { content }

                        output.append(content)
                        for (char in content) {
                            when (char) {
                                '[' -> {
                                    if (!capturing) {
                                        capturing = true
                                        isAtSymbolDetected = false
                                    }
                                    openBrackets++
                                }
                                '@' -> {
                                    if (capturing && expression.isEmpty()) {
                                        isAtSymbolDetected = true
                                    }
                                }
                                ']' -> {
                                    if (openBrackets > 0) {
                                        openBrackets--
                                        if (openBrackets == 0) {
                                            expression.append(char)
                                            val result = if (isAtSymbolDetected) {
                                                Expression(expression.toString())
                                            } else {
                                                ToolCall(expression.toString())
                                            }
                                            capturing = false
                                            isAtSymbolDetected = false
                                            deferredResult.complete(result)
                                            cancel()
                                        }
                                    }
                                }
                            }
                        }

                        if (capturing) {
                            expression.append(content)
                        }
                    }

                deferredResult.complete(Stop(output.toString()))
            } catch (_: CancellationException) {
                logger?.trace { "\nFlow was cancelled as expected." }
                deferredResult.complete(Stop("Cancelled early"))
            } catch (e: ConnectException) {
                logger?.error { "\nCould not connect: ${e.message}" }
                deferredResult.complete(Stop("Error: ${e.message}"))
            } catch (e: Exception) {
                logger?.error { "\nFlow threw an unexpected exception: ${e.message}" }
                deferredResult.complete(Stop("Error: ${e.message}"))
            }
        }

        return deferredResult.await()
    }
}
