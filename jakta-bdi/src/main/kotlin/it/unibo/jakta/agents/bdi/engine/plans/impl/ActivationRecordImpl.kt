package it.unibo.jakta.agents.bdi.engine.plans.impl

import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ActivationRecord")
internal data class ActivationRecordImpl(
    override val goalQueue: List<Goal>,
    override val plan: PlanID,
) : ActivationRecord {
    override fun pop(): ActivationRecord = copy(goalQueue = goalQueue - goalQueue.first())

    override fun applySubstitution(substitution: Substitution): ActivationRecord =
        copy(goalQueue = goalQueue.map { it.applySubstitution(substitution) })

    override fun isLastGoal(): Boolean = goalQueue.size == 1
}
