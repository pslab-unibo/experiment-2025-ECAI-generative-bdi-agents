package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.BynaryResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.BinaryAnswerParserFailure.InvalidResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.BinaryAnswerParserSuccess
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.BinaryAnswerParserSuccess.AffirmativeResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.BinaryAnswerParserSuccess.NegativeResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult

class BinaryResponseParserImpl : BynaryResponseParser {
    private val buffer = StringBuilder()
    private var completed = false
    private var result: BinaryAnswerParserSuccess? = null

    private val positiveWords =
        setOf("yes", "yeah", "yep", "correct", "right", "affirmative", "confirmed", "true", "done")

    override fun parse(input: Char) {
        if (!completed) {
            buffer.append(input)
            checkForCompletion()
        }
    }

    override fun parse(input: String) = input.forEach { parse(it) }

    private fun checkForCompletion() {
        val currentText = buffer.toString().trim().lowercase()
        val normalizedText = currentText.replace(Regex("[^a-z]"), "")

        for (word in positiveWords) {
            if (normalizedText.contains(word)) {
                completed = true
                result = AffirmativeResponse(currentText)
                return
            }
        }
        result = NegativeResponse(currentText)
    }

    override fun buildResult(): ParserResult = result ?: InvalidResponse("Unknown binary response")

    override fun isComplete(): Boolean = completed

    override fun reset() {
        buffer.clear()
        completed = false
        result = null
    }
}
