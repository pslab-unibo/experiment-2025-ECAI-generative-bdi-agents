package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

class PlanLibraryUpdater(
    override val logger: KLogger?,
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
