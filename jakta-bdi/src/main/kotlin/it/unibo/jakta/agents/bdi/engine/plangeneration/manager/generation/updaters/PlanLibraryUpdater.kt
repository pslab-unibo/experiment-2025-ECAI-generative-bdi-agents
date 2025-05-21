package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.updaters

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

class PlanLibraryUpdater(
    override val logger: AgentLogger?,
) : Updater {
    fun update(
        context: AgentContext,
        planGenResult: PlanGenerationResult,
    ): PlanLibrary {
        val generatedPlans = planGenResult.generatedPlanLibrary

        val nGeneratedPlans = generatedPlans.size
        when (nGeneratedPlans) {
            0 -> logger?.info { "No additional plans generated" }
            1 -> logger?.info { "Added one generated plan" }
            else -> logger?.info { "Added $nGeneratedPlans generated plans" }
        }

        // The plus operator handles overwriting existing plans.
        return context.planLibrary + PlanLibrary.of(generatedPlans)
    }
}
