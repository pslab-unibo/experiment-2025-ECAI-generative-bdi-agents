package it.unibo.jakta.agents.bdi.plans

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.plans.impl.PlanLibraryImpl

interface PlanLibrary {
    /**
     * Like a standard practice in prolog, plans are ordered to let programmers know when the end of an eventual recursion happens.
     */
    val plans: List<Plan>

    /** @return all the relevant [Plan]s from a given [Event] */
    fun relevantPlans(event: Event): PlanLibrary

    /** @return all the applicable [Plan]s in the agent with the specified [BeliefBase] */
    fun applicablePlans(event: Event, beliefBase: BeliefBase): PlanLibrary

    fun getPlan(planID: PlanID): Plan?

    fun addPlan(plan: Plan): PlanLibrary

    fun addPlan(planID: PlanID): PlanLibrary

    fun removePlan(plan: Plan): PlanLibrary

    fun removePlan(
        planID: PlanID,
        predicate: (Plan) -> Boolean = { true },
    ): PlanLibrary

    fun updatePlan(plan: Plan): PlanLibrary

    // If duplicate plans with the same context are found, the ones from the other plan library take precedence.
    operator fun plus(other: PlanLibrary) = of((other.plans + this.plans).distinctBy { it.id })

    companion object {
        fun of(plans: List<Plan>): PlanLibrary = PlanLibraryImpl(plans)
        fun of(vararg plans: Plan): PlanLibrary = of(plans.asList())

        fun empty(): PlanLibrary = PlanLibraryImpl(emptyList())
    }
}
