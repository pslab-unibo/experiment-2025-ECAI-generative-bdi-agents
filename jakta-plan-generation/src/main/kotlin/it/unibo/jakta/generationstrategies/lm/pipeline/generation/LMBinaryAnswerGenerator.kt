package it.unibo.jakta.generationstrategies.lm.pipeline.generation

import it.unibo.jakta.generationstrategies.lm.pipeline.generation.impl.LMBinaryAnswerGeneratorImpl
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler

interface LMBinaryAnswerGenerator : LMGenerator {
    companion object {
        fun of(
            requestHandler: RequestHandler,
            responseParser: ResponseParser,
        ): LMBinaryAnswerGenerator =
            LMBinaryAnswerGeneratorImpl(requestHandler, responseParser)
    }
}
