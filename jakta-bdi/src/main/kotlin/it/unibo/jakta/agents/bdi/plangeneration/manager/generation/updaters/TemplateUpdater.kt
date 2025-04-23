package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult

class TemplateUpdater(
    override val logger: KLogger?,
) : Updater {
    /**
     * Updates the list of admissible beliefs in the given context with those
     * generated from the plan generation result.
     * Overwrites the already existing admissible beliefs in the context with the newer ones.
     */
    fun updateAdmissibleBeliefs(
        context: AgentContext,
        planGenResult: PlanGenerationResult,
    ): Set<AdmissibleBelief> {
        val newAdmissibleBeliefs = planGenResult.generatedAdmissibleBeliefs
        val oldAdmissibleBeliefs = context.admissibleBeliefs
        return (newAdmissibleBeliefs + oldAdmissibleBeliefs).distinctBy { it.rule }.toSet()
    }

    /**
     * Updates the list of admissible goals in the given context with those
     * generated from the plan generation result.
     * Overwrites the already existing admissible goals in the context with the newer ones.
     */
    fun updateAdmissibleGoals(
        context: AgentContext,
        planGenResult: PlanGenerationResult,
    ): Set<AdmissibleGoal> {
        val newAdmissibleGoals = planGenResult.generatedAdmissibleGoals
        val oldAdmissibleGoals = context.admissibleGoals
        return (newAdmissibleGoals + oldAdmissibleGoals).distinctBy { it.trigger }.toSet()
    }
}
