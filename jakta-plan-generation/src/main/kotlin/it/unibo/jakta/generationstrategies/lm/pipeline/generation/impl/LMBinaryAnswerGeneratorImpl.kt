package it.unibo.jakta.generationstrategies.lm.pipeline.generation.impl

import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.generationstrategies.lm.LMBinaryResult
import it.unibo.jakta.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMBinaryAnswerGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.BinaryAnswerParserFailure.InvalidResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.BinaryAnswerParserSuccess.AffirmativeResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.BinaryAnswerParserSuccess.NegativeResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy

class LMBinaryAnswerGeneratorImpl(
    override val requestHandler: RequestHandler,
    override val responseParser: ResponseParser,
) : LMBinaryAnswerGenerator {

    override fun handleParserResults(
        generatingPlan: PartialPlan,
        generationStrategy: LMGenerationStrategy,
        generationResults: ParserResult,
        generationState: LMGenerationState,
    ): GenerationResult =
        when (generationResults) {
            is AffirmativeResponse -> LMBinaryResult(true)
            is NegativeResponse -> LMBinaryResult(false)
            is InvalidResponse -> {
                val errorMsg =
                    "Please answer strictly with 'yes' or 'no' only, then provide a short explanation."
                handleParsingFailure(errorMsg, generationState)
            }
            else -> LMGenerationFailure(
                generationState,
                "Unknown parser response",
            )
        }
}
