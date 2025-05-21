package it.unibo.jakta.agents.bdi.engine.intentions.impl

import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.intentions.IntentionID
import it.unibo.jakta.agents.bdi.engine.plans.ActivationRecord
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.Serializable
import kotlin.collections.plus

@Serializable
internal class IntentionImpl(
    override val recordStack: List<ActivationRecord>,
    override val isSuspended: Boolean,
    override val id: IntentionID,
) : Intention {
    override fun push(activationRecord: ActivationRecord): Intention =
        this.copy(recordStack = listOf(activationRecord) + recordStack)

    override fun applySubstitution(substitution: Substitution): Intention {
        val record = recordStack.first()
        return this.copy(recordStack = listOf(record.applySubstitution(substitution)) + recordStack - record)
    }

    override fun pop(): Intention {
        val record = recordStack.first()
        return if (record.isLastGoal()) {
            this.copy(recordStack = recordStack - record)
        } else {
            this.copy(recordStack = listOf(record.pop()) + recordStack - record)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IntentionImpl

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

    override fun toString(): String = "IntentionImpl(recordStack=$recordStack, isSuspended=$isSuspended, id=$id)"
}
