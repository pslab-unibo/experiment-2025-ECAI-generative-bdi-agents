package it.unibo.jakta.agents.bdi.intentions.impl

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.intentions.GoalTrackingIntention
import it.unibo.jakta.agents.bdi.intentions.IntentionID
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.PlanID

internal class GoalTrackingIntentionImpl(
    override val recordStack: List<ActivationRecord>,
    override val isSuspended: Boolean = false,
    override val id: IntentionID = IntentionID(),
    override val generatedPlanStack: List<PlanID> = emptyList(),
    override val goalAchievedTrace: List<Goal> = emptyList(),
) : BaseIntention(recordStack, isSuspended, id), GoalTrackingIntention {

    override fun pop(): GoalTrackingIntention {
        val record = recordStack.first()
        val completedGoal = record.goalQueue.first()
        return if (record.isLastGoal()) {
            this.copy(
                recordStack = recordStack - record,
                goalsAchieved = goalAchievedTrace + completedGoal,
            )
        } else {
            this.copy(
                recordStack = listOf(record.pop()) + recordStack - record,
                goalsAchieved = goalAchievedTrace + completedGoal,
            )
        }
    }

    override fun copy(
        recordStack: List<ActivationRecord>,
        isSuspended: Boolean,
        id: IntentionID,
    ): GoalTrackingIntention {
        return GoalTrackingIntentionImpl(
            recordStack = recordStack,
            isSuspended = isSuspended,
            id = id,
            generatedPlanStack = generatedPlanStack,
            goalAchievedTrace = goalAchievedTrace,
        )
    }
}
