package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.LiteratePrologParser.DelimiterType
import it.unibo.jakta.generationstrategies.lm.Expression
import it.unibo.jakta.generationstrategies.lm.Failure
import it.unibo.jakta.generationstrategies.lm.GenerationResult
import it.unibo.jakta.generationstrategies.lm.Goal
import it.unibo.jakta.generationstrategies.lm.Precondition
import it.unibo.jakta.generationstrategies.lm.Stop
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class StreamProcessor(val api: OpenAI) {
    private val jaktaKeywords = setOf(
        "achieve",
        "test",
        "add",
        "remove",
        "update",
        "execute",
    )

    suspend fun getChatCompletionResult(
        logger: KLogger?,
        request: ChatCompletionRequest,
        delimiterType: DelimiterType = DelimiterType.BACKTICKS,
    ): GenerationResult {
        val deferredResult = CompletableDeferred<GenerationResult>()
        val job = CoroutineScope(Dispatchers.IO).launch {
            try {
                var openBrackets = 0
                var backtickCount = 0
                var isComplete = false
                val outputBuffer = StringBuilder()
                val expressionBuffer = StringBuilder()

                api.chatCompletions(request)
                    .collect { response ->
                        val content = response.choices.first().delta?.content.orEmpty()
                        print(content)
                        outputBuffer.append(content)

                        for (char in content) {
                            when (delimiterType) {
                                DelimiterType.SQUARE_BRACKETS -> {
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
                                                expressionBuffer.append(char)
                                                if (openBrackets == 0) {
                                                    val result = expressionBuffer.toString()
                                                    isComplete = false
                                                    val parsingResult =
                                                        ParserResultImpl(outputBuffer.toString(), result)
                                                    val res = handleFragment(parsingResult)
                                                    when (res) {
                                                        is Precondition, is Expression -> {}
                                                        is Failure, is Goal, is Stop -> {
                                                            deferredResult.complete(res)
                                                            cancel()
                                                        }
                                                    }
                                                    expressionBuffer.clear()
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
                                DelimiterType.BACKTICKS -> {
                                    when (char) {
                                        '`' -> {
                                            backtickCount++
                                            if (backtickCount == 1) {
                                                // First backtick encountered
                                                if (!isComplete) {
                                                    isComplete = true
                                                    expressionBuffer.append(char)
                                                }
                                            } else if (backtickCount == 2) {
                                                // Second backtick encountered
                                                expressionBuffer.append(char)
                                                val result = expressionBuffer.toString()
                                                isComplete = false
                                                backtickCount = 0
                                                val parsingResult = ParserResultImpl(outputBuffer.toString(), result)
                                                val res = handleFragment(parsingResult)
                                                when (res) {
                                                    is Precondition, is Expression -> {}
                                                    is Failure, is Goal, is Stop -> {
                                                        deferredResult.complete(res)
                                                        cancel()
                                                    }
                                                }
                                                expressionBuffer.clear()
                                            }
                                        }
                                        else -> {
                                            if (isComplete) {
                                                expressionBuffer.append(char)
                                            } else {
                                                // Reset backtick counter when we see other characters outside expression
                                                backtickCount = 0
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                deferredResult.complete(Stop(outputBuffer.toString()))
            } catch (_: CancellationException) {
                logger?.trace { "\nFlow was cancelled." }
                deferredResult.complete(Failure("Cancelled early"))
            } catch (e: Exception) {
                logger?.error { "\nFlow threw an unexpected exception: ${e.message}" }
                deferredResult.complete(Failure("Error: ${e.message}"))
            }
        }

        job.join()
        return deferredResult.await()
    }

    private tailrec fun handleFragment(fragment: ParserResultImpl): GenerationResult {
        val fragmentToParse = fragment.fragmentToParse
        val msgContent = fragment.content
        return when {
            fragmentToParse.startsWith("!") -> handleFragment(
                fragment.copy(fragmentToParse = fragmentToParse.replace("!", "not(") + ")"),
            )
            fragmentToParse.startsWith("@") -> Expression(msgContent, fragmentToParse.substring(1))

            jaktaKeywords.any { keyword ->
                fragmentToParse.startsWith("`$keyword(") && fragmentToParse.endsWith(")`")
            } -> Goal(msgContent, fragmentToParse)

            else -> Precondition(msgContent, fragmentToParse)
        }
    }
}
