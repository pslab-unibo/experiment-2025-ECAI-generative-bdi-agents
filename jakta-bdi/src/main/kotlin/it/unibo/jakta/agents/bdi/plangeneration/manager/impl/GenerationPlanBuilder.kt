package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.tuprolog.core.Struct

object GenerationPlanBuilder {
    fun createNewTriggerFromGoal(goal: Goal): Trigger? =
        when (goal) {
            is Achieve -> AchievementGoalInvocation(goal.value)
            is Test -> TestGoalInvocation(goal.belief)
            else -> null
        }

    fun createNewGoalFromTrigger(trigger: Trigger): Goal? =
        when (trigger) {
            is AchievementGoalInvocation -> Achieve.of(trigger.value)
            is TestGoalInvocation -> Test.of(trigger.belief)
            else -> null
        }

    fun getFailureTrigger(trigger: Trigger): Trigger? =
        when (trigger) {
            is AchievementGoalInvocation -> AchievementGoalFailure(trigger.value)
            is TestGoalInvocation -> TestGoalFailure(trigger.value)
            else -> null
        }

    fun getGenerationPlanID(trigger: Trigger) =
        PlanID(
            trigger = trigger,
            context = Belief.fromSelfSource(
                Struct.of("missing_plan_for", trigger.value),
            ).rule.head,
        )

    fun getGenerationPlan(trigger: Trigger): PartialPlan? {
        val goal = createNewGoalFromTrigger(trigger)
        val failureTrigger = getFailureTrigger(trigger)
        return if (goal != null && failureTrigger != null) {
            val initialGoal = GeneratePlan.of(goal)
            PartialPlan.of(
                parentGenerationGoal = initialGoal,
                id = getGenerationPlanID(failureTrigger),
                goals = listOf(initialGoal, goal),
            )
        } else {
            null
        }
    }
}
