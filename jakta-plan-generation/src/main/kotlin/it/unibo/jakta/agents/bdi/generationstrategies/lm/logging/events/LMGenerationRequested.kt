package it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events

import it.unibo.jakta.agents.bdi.engine.logging.events.PlanGenProcedureEvent
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("LMGenerationRequested")
data class LMGenerationRequested(
    val genConfig: LMGenerationConfig,
    override val description: String,
) : PlanGenProcedureEvent {
    constructor(genConfig: LMGenerationConfig) : this(genConfig, "LM Generation requested")
}
