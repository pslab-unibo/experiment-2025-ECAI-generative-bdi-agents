package it.unibo.jakta.agents.bdi.intentions

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.intentions.impl.DeclarativeIntentionImpl
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID

/**
 * A declarative intention is an intention in which there is at least one or more record stacks
 * of partial plans and at least a generation process is running. Since intentions run concurrently,
 * more than one generation process might be running. Each intention has a reference to the process
 * it is currently running. In addition, since generation processes can be nested in others,
 * it stores also the ones that started the current, using a list as a stack.
 */
interface DeclarativeIntention : Intention {

    val generatingPlans: List<PlanID>

    fun currentGeneratingPlan(): PlanID? = generatingPlans.firstOrNull()

    override fun pop(): DeclarativeIntention

    override fun copy(
        recordStack: List<ActivationRecord>,
        isSuspended: Boolean,
        id: IntentionID,
    ): DeclarativeIntention = of(recordStack, isSuspended, id)

    fun copy(
        recordStack: List<ActivationRecord> = this.recordStack,
        isSuspended: Boolean = this.isSuspended,
        id: IntentionID = this.id,
        generatingPlans: List<PlanID> = this.generatingPlans,
    ): DeclarativeIntention =
        of(recordStack, isSuspended, id, generatingPlans)

    companion object {
        fun of(plan: Plan): DeclarativeIntention = of(listOf(plan.toActivationRecord()))

        fun of(
            recordStack: List<ActivationRecord> = emptyList(),
            isSuspended: Boolean = false,
            id: IntentionID = IntentionID(),
            generatingPlans: List<PlanID> = emptyList(),
            goalsAchieved: List<Goal> = emptyList(),
        ): DeclarativeIntention = DeclarativeIntentionImpl(
            recordStack,
            isSuspended,
            id,
            generatingPlans,
        )

        fun Intention.toTrackingIntention(): DeclarativeIntention = of(
            this.recordStack,
            this.isSuspended,
            this.id,
        )

        fun DeclarativeIntention.toIntention(): Intention = Intention.of(
            this.recordStack,
            this.isSuspended,
            this.id,
        )

        /**
         * Removes the previous goal and adds a new goal immediately after the removed one.
         */
        fun Intention.replace(previousGoal: Goal, newGoal: Goal): Intention {
            return updateCurrentRecordStack { goalQueue ->
                goalQueue.map { if (it == previousGoal) newGoal else it }
            }
        }

        /**
         * Add a new goal immediately after the previous one.
         */
        fun Intention.append(previousGoal: Goal, vararg newGoals: Goal): Intention {
            return updateCurrentRecordStack { goalQueue ->
                goalQueue.flatMap {
                    if (it == previousGoal) listOf(it, *newGoals) else listOf(it)
                }
            }
        }

        private fun <T : Intention> T.updateCurrentRecordStack(
            transform: (List<Goal>) -> List<Goal>,
        ): T {
            val currentRecord = recordStack.first()
            val updatedGoalQueue = transform(currentRecord.goalQueue)
            val newActivationRecord = ActivationRecord.of(updatedGoalQueue, currentRecord.plan)
            val newRecordStack = listOf(newActivationRecord) + recordStack.drop(1)

            @Suppress("UNCHECKED_CAST")
            return copy(recordStack = newRecordStack) as T
        }
    }
}
