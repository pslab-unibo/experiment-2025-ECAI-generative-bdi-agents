package it.unibo.jakta.agents.bdi.plangeneration.feedback

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID

sealed interface SuccessFeedback : ExecutionFeedback

data class PlanExecutionCompleted(
    val planID: PlanID,
    val goalsAchieved: List<Goal> = emptyList(),
) : SuccessFeedback {
    val trigger = formatter.format(planID.trigger.value)

    override val description = "Completed plan that branched from $trigger"

    override val metadata = super.metadata + buildMap {
        put("trigger", planID.trigger)
        put("guard", planID.guard)
    }
}

data class GenerationExecuted(
    val msg: String,
    override val description: String = msg,
) : SuccessFeedback

data class GenerationCompleted(
    val basePlan: Plan,
    val additionalPlans: List<Plan>,
) : SuccessFeedback {
    override val description =
        "Completed generation for goal ${formatter.format(basePlan.trigger.value)}"

    override val metadata: Map<String, Any?> = super.metadata + buildMap {
        put("basePlan", basePlan)
        put("additionalPlans", additionalPlans)
    }
}

sealed interface GoalSuccess : SuccessFeedback

data class GoalExecutionSuccess(
    val goalExecuted: Goal,
) : GoalSuccess {
    override val description =
        "Goal $goalExecuted executed successfully"
}
