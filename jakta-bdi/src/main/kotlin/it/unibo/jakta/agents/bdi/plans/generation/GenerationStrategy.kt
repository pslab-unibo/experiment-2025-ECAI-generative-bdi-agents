package it.unibo.jakta.agents.bdi.plans.generation

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan

interface GenerationStrategy {
    val lmGenCfg: GenerationConfig

    fun requestPlanGeneration(generatedPlan: GeneratedPlan): PlanGenerationResult

    fun parseResponse(
        logger: KLogger,
        response: String,
    ): PlanGenerationResult
}
