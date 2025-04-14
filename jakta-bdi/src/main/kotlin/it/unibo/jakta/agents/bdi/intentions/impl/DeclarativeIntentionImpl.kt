package it.unibo.jakta.agents.bdi.intentions.impl

import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.IntentionID
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.PlanID

internal class DeclarativeIntentionImpl(
    override val recordStack: List<ActivationRecord>,
    override val isSuspended: Boolean,
    override val id: IntentionID,
    override val generatingPlans: List<PlanID>,
) : BaseIntention(recordStack, isSuspended, id), DeclarativeIntention {

    override fun pop(): DeclarativeIntention {
        val record = recordStack.first()
        return if (record.isLastGoal()) {
            this.copy(recordStack = recordStack - record)
        } else {
            this.copy(recordStack = listOf(record.pop()) + recordStack - record)
        }
    }

    override fun copy(
        recordStack: List<ActivationRecord>,
        isSuspended: Boolean,
        id: IntentionID,
    ): DeclarativeIntention {
        return DeclarativeIntentionImpl(
            recordStack = recordStack,
            isSuspended = isSuspended,
            id = id,
            generatingPlans = generatingPlans,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DeclarativeIntentionImpl

        if (isSuspended != other.isSuspended) return false
        if (recordStack != other.recordStack) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isSuspended.hashCode()
        result = 31 * result + recordStack.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun toString(): String {
        return "TrackingIntentionImpl(recordStack=$recordStack," +
            " isSuspended=$isSuspended," +
            " id=$id)"
    }
}
