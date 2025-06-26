package it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.updaters

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.generation.GenerationResult
import it.unibo.jakta.agents.bdi.engine.generation.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger

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
