package it.unibo.jakta.agents.bdi.engine.plangeneration.manager.generation.updaters

import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger
import it.unibo.jakta.agents.bdi.engine.plangeneration.PlanGenerationResult

class TemplateUpdater(
    override val logger: AgentLogger?,
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
        return (oldAdmissibleBeliefs + newAdmissibleBeliefs).distinctBy { it.rule.head.functor }.toSet()
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
        return (oldAdmissibleGoals + newAdmissibleGoals).distinctBy { it.trigger.value.functor }.toSet()
    }
}
