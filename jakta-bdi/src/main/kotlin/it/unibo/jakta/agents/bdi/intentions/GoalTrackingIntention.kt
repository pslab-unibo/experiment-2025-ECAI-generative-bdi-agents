package it.unibo.jakta.agents.bdi.intentions

import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.intentions.impl.GoalTrackingIntentionImpl
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID

interface GoalTrackingIntention : Intention {
    val generatedPlanStack: List<PlanID>
    val goalAchievedTrace: List<Goal>

    fun isCurrentPlanGenerated(): Boolean =
        generatedPlanStack.contains(currentPlan())
    fun isCurrentPlanLastGeneratedOne(): Boolean =
        generatedPlanStack.lastOrNull() == currentPlan() && generatedPlanStack.size == 1

    fun copy(
        recordStack: List<ActivationRecord> = this.recordStack,
        isSuspended: Boolean = this.isSuspended,
        id: IntentionID = this.id,
        generatedPlanStack: List<PlanID> = this.generatedPlanStack,
        goalsAchieved: List<Goal> = this.goalAchievedTrace,
    ): GoalTrackingIntention = of(recordStack, isSuspended, id, generatedPlanStack, goalsAchieved)

    companion object {
        fun of(plan: Plan): Intention = GoalTrackingIntentionImpl(listOf(plan.toActivationRecord()))

        fun of(
            recordStack: List<ActivationRecord> = emptyList(),
            isSuspended: Boolean = false,
            id: IntentionID = IntentionID(),
            generatedPlanStack: List<PlanID> = emptyList(),
            goalsAchieved: List<Goal> = emptyList(),
        ): GoalTrackingIntention = GoalTrackingIntentionImpl(
            recordStack,
            isSuspended,
            id,
            generatedPlanStack,
            goalsAchieved,
        )

        fun fromIntention(intention: Intention): GoalTrackingIntention = GoalTrackingIntentionImpl(
            intention.recordStack,
            intention.isSuspended,
            intention.id,
        )

        fun toIntention(trackingIntention: GoalTrackingIntention): Intention = Intention.of(
            trackingIntention.recordStack,
            trackingIntention.isSuspended,
            trackingIntention.id,
        )
    }
}
