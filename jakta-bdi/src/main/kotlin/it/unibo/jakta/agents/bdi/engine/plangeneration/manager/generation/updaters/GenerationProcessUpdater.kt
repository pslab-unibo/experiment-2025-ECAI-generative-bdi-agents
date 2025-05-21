package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.updaters

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.registry.GenerationProcessRegistry

class GenerationProcessUpdater(
    override val logger: AgentLogger?,
) : Updater {
    fun update(
        context: AgentContext,
        planGenResult: GenerationResult,
    ): GenerationProcessRegistry =
        context
            .generationProcesses
            .updateGenerationProcess(planGenResult.generationState)
}
