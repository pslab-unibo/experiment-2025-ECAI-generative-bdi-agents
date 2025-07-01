package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation

import it.unibo.jakta.agents.bdi.engine.generation.GenerationResult
import it.unibo.jakta.agents.bdi.engine.generation.GenerationState
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.generation.Generator
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy

interface LMGenerator : Generator {
    val requestHandler: RequestHandler
    val responseParser: Parser

    fun handleRequestResult(
        generationStrategy: LMGenerationStrategy,
        requestResult: RequestResult,
        generationState: LMGenerationState,
    ): GenerationResult

    suspend fun generate(
        generationStrategy: GenerationStrategy,
        generationState: GenerationState,
    ): GenerationResult {
        val lmGenStrat = generationStrategy as? LMGenerationStrategy
        val lmGenState = generationState as? LMGenerationState

        return when {
            lmGenStrat == null ->
                LMGenerationFailure(
                    generationState,
                    "Expected a LMGenerationStrategy but got ${generationStrategy.javaClass.simpleName}",
                )
            lmGenState == null ->
                LMGenerationFailure(
                    generationState,
                    "Expected a LMGenerationState but got ${generationState.javaClass.simpleName}",
                )
            else -> {
                val generationResult = requestHandler.requestTextCompletion(generationState, responseParser)
                handleRequestResult(generationStrategy, generationResult, generationState)
            }
        }
    }
}
