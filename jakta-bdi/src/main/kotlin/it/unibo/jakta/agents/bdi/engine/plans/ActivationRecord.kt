package it.unibo.jakta.agents.bdi.engine.plans

import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.impl.ActivationRecordImpl
import it.unibo.tuprolog.core.Substitution

interface ActivationRecord {
    val goalQueue: List<Goal>

    val plan: PlanID

    fun pop(): ActivationRecord

    fun applySubstitution(substitution: Substitution): ActivationRecord

    fun isLastGoal(): Boolean

    companion object {
        fun of(
            goals: List<Goal>,
            plan: PlanID,
        ): ActivationRecord = ActivationRecordImpl(goals, plan)
    }
}
