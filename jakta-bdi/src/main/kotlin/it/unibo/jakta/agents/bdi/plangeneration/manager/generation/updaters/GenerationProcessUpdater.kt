package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.registry.GenerationProcessRegistry

class GenerationProcessUpdater(
    override val logger: KLogger?,
) : Updater {

    fun update(
        context: AgentContext,
        genGoal: GeneratePlan,
        planGenResult: GenerationResult,
    ): GenerationProcessRegistry =
        context
            .generationProcesses
            .updateGenerationProcess(genGoal, planGenResult.generationState)
}
