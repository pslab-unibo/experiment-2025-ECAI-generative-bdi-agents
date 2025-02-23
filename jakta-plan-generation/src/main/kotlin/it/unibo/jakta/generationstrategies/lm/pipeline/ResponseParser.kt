package it.unibo.jakta.generationstrategies.lm.pipeline

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.plans.generation.PlanGenerationResult

interface ResponseParser {
    fun parse(logger: KLogger, response: String): PlanGenerationResult
}
