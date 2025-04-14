package it.unibo.jakta.agents.bdi.intentions.impl

import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.intentions.IntentionID
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.tuprolog.core.Substitution

abstract class BaseIntention(
    override val recordStack: List<ActivationRecord>,
    override val isSuspended: Boolean = false,
    override val id: IntentionID,
) : Intention {

    override fun push(activationRecord: ActivationRecord): Intention {
        return this.copy(recordStack = listOf(activationRecord) + recordStack)
    }

    override fun applySubstitution(substitution: Substitution): Intention {
        val record = recordStack.first()
        return this.copy(recordStack = listOf(record.applySubstitution(substitution)) + recordStack - record)
    }
}
