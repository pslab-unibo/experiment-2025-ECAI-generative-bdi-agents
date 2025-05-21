package it.unibo.jakta.agents.bdi.engine.plans.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

internal data class PlanLibraryImpl(
    override val plans: List<Plan>,
) : PlanLibrary {
    override fun relevantPlans(event: Event): PlanLibrary = PlanLibrary.of(plans.filter { it.isRelevant(event) })

    override fun applicablePlans(
        event: Event,
        beliefBase: BeliefBase,
    ): PlanLibrary = PlanLibrary.of(plans.filter { it.isApplicable(event, beliefBase) })

    override fun getPlan(planID: PlanID): Plan? = plans.firstOrNull { it.id == planID }

    override fun addPlan(plan: Plan): PlanLibrary = PlanLibrary.of((plans + plan).distinctBy { it.id })

    override fun addPlan(planID: PlanID): PlanLibrary =
        plans.firstOrNull { it.id == planID }?.let { addPlan(it) } ?: this

    override fun updatePlan(plan: Plan): PlanLibrary {
        val planID = plan.id
        val filteredPlans = plans.filter { it.id != planID }
        return PlanLibrary.of(filteredPlans + plan)
    }

    override fun removePlan(plan: Plan): PlanLibrary = PlanLibrary.of(plans - plan)

    override fun removePlan(
        planID: PlanID,
        predicate: (Plan) -> Boolean,
    ): PlanLibrary {
        val planToRemove = plans.firstOrNull { it.id == planID && predicate(it) }
        return if (planToRemove != null) removePlan(planToRemove) else this
    }
}
