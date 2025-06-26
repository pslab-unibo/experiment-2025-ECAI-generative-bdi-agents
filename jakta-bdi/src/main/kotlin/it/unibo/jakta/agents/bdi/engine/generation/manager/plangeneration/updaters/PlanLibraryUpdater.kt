package it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.updaters

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

class PlanLibraryUpdater(
    override val logger: AgentLogger?,
) : Updater {
    fun update(
        context: AgentContext,
        planGenResult: PlanGenerationResult,
    ): PlanLibrary {
        val generatedPlans = planGenResult.generatedPlanLibrary
        // The plus operator handles overwriting existing plans.
        return context.planLibrary + PlanLibrary.of(generatedPlans)
    }
}
