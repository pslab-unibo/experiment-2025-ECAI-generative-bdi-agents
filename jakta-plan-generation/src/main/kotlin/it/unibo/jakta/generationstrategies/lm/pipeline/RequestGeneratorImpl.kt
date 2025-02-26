package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.client.OpenAI
import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.generationstrategies.lm.Expression
import it.unibo.jakta.generationstrategies.lm.Failure
import it.unibo.jakta.generationstrategies.lm.GenerationResult
import it.unibo.jakta.generationstrategies.lm.Goal
import it.unibo.jakta.generationstrategies.lm.Precondition
import it.unibo.jakta.generationstrategies.lm.Stop
import kotlinx.coroutines.runBlocking

class RequestGeneratorImpl(val api: OpenAI) : RequestGenerator {
    val processor = StreamProcessor(api)
    private val jaktaKeywords = setOf(
        "achieve",
        "test",
        "add",
        "remove",
        "update",
        "execute",
    )

    override suspend fun requestTextCompletion(logger: KLogger?, request: ChatCompletionRequest): GenerationResult =
        runBlocking {
            val parsingResult = processor.getChatCompletionResult(logger, request)
            handleParsingResult(parsingResult)
        }

    private fun handleParsingResult(result: ParsingResult): GenerationResult =
        when (result) {
            is ParserFail -> Failure(result.content)
            is ParserSuccess -> handleFragment(result)
            is ParserStop -> Stop(result.content)
        }

    private tailrec fun handleFragment(fragment: ParserSuccess): GenerationResult {
        val fragmentToParse = fragment.fragmentToParse
        val msgContent = fragment.content
        return when {
            fragmentToParse.startsWith("!") -> handleFragment(
                fragment.copy(fragmentToParse = fragmentToParse.replace("!", "not(") + ")"),
            )
            fragmentToParse.startsWith("@") -> Expression(msgContent, fragmentToParse.substring(1))

            jaktaKeywords.any { keyword ->
                fragmentToParse.startsWith("[$keyword(") && fragmentToParse.endsWith(")]")
            } -> Goal(msgContent, fragmentToParse)

            else -> Precondition(msgContent, fragmentToParse)
        }
    }
}
