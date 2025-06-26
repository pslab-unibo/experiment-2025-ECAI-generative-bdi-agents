package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result

import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

sealed interface RequestSuccess : RequestResult {
    data class NewResult(
        val plans: List<NewPlan> = emptyList(),
        val admissibleGoals: Set<AdmissibleGoal> = emptySet(),
        val admissibleBeliefs: Set<AdmissibleBelief> = emptySet(),
        override val rawContent: String,
        val parsingErrors: List<ParserFailure> = emptyList(),
    ) : RequestSuccess

    data class NewPlan(
        val id: PlanID? = null,
        val trigger: Trigger,
        val guard: Struct = Truth.TRUE,
        val goals: List<Goal>,
    ) {
        override fun toString(): String = Plan.formatPlanToString(trigger, guard, goals)
    }
}
