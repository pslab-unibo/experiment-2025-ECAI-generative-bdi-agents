package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation

import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation.impl.LMPlanGeneratorImpl
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler

interface LMPlanGenerator : LMGenerator {
    companion object {
        fun of(
            requestHandler: RequestHandler,
            responseParser: Parser,
        ): LMPlanGenerator = LMPlanGeneratorImpl(requestHandler, responseParser)
    }
}
