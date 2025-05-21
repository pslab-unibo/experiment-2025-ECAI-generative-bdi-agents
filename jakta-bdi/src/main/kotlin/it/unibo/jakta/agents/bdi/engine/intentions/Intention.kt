package it.unibo.jakta.agents.bdi.engine.intentions

import it.unibo.jakta.agents.bdi.engine.goals.Goal
import it.unibo.jakta.agents.bdi.engine.intentions.impl.IntentionImpl
import it.unibo.jakta.agents.bdi.engine.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.tuprolog.core.Substitution

interface Intention {
    val recordStack: List<ActivationRecord>

    val isSuspended: Boolean

    val id: IntentionID

    fun nextGoal(): Goal = recordStack.first().goalQueue.first()

    fun currentPlan(): PlanID = recordStack.first().plan

    /**
     * Removes the first goal to be executed from the first activation record. If the goal is the last one,
     * then the whole activation record is removed from the record stack.
     */
    fun pop(): Intention

    fun push(activationRecord: ActivationRecord): Intention

    fun applySubstitution(substitution: Substitution): Intention

    fun copy(
        recordStack: List<ActivationRecord> = this.recordStack,
        isSuspended: Boolean = this.isSuspended,
        id: IntentionID = this.id,
    ): Intention = of(recordStack, isSuspended, id)

    companion object {
        fun of(plan: Plan): Intention = of(listOf(plan.toActivationRecord()))

        fun of(
            recordStack: List<ActivationRecord> = emptyList(),
            isSuspended: Boolean = false,
            id: IntentionID = IntentionID(),
        ): Intention = IntentionImpl(recordStack, isSuspended, id)

        /**
         * Removes the previous goal and adds a new goal immediately after the removed one.
         */
        fun Intention.replace(
            previousGoal: Goal,
            newGoal: Goal,
        ): Intention =
            updateCurrentRecordStack { goalQueue ->
                goalQueue.map { if (it == previousGoal) newGoal else it }
            }

        private fun <T : Intention> T.updateCurrentRecordStack(transform: (List<Goal>) -> List<Goal>): T {
            val currentRecord = recordStack.first()
            val updatedGoalQueue = transform(currentRecord.goalQueue)
            val newActivationRecord = ActivationRecord.of(updatedGoalQueue, currentRecord.plan)
            val newRecordStack = listOf(newActivationRecord) + recordStack.drop(1)

            @Suppress("UNCHECKED_CAST")
            return copy(recordStack = newRecordStack) as T
        }
    }
}
