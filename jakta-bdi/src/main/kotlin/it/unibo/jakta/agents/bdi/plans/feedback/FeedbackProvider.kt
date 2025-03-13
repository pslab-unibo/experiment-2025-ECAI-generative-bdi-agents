package it.unibo.jakta.agents.bdi.plans.feedback

import it.unibo.jakta.agents.bdi.intentions.GoalTrackingIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

object FeedbackProvider {
    fun provideFeedback(
        intention: Intention,
        feedback: GenerationFeedback,
        planLibrary: PlanLibrary,
    ): PlanLibrary {
        val id = intention.currentPlan()
        return provideFeedback(id, feedback, planLibrary)
    }

    fun provideFeedback(
        intention: GoalTrackingIntention,
        feedback: GenerationFeedback,
        planLibrary: PlanLibrary,
    ): PlanLibrary {
        val id = intention.currentPlan()
        val planID = intention.generatedPlanStack.firstOrNull { it == id }
        return if (planID != null) {
            provideFeedback(planID, feedback, planLibrary)
        } else {
            planLibrary
        }
    }

    private fun provideFeedback(
        planID: PlanID,
        feedback: GenerationFeedback,
        planLibrary: PlanLibrary,
    ): PlanLibrary {
        val genPlan = planLibrary.plans.firstOrNull { it.id == planID }
        return if (genPlan != null && genPlan is GeneratedPlan) {
            val strategyWithFeedback = genPlan.generationStrategy?.provideGenerationFeedback(feedback)
            val planWithFeedback = GeneratedPlan.Companion.of(
                genPlan.id,
                genPlan.trigger,
                genPlan.guard,
                genPlan.goals,
                strategyWithFeedback,
                genPlan.literateTrigger,
                genPlan.literateGuard,
                genPlan.literateGoals,
            )
            PlanLibrary.Companion.of(planLibrary.plans).removePlan(genPlan).addPlan(planWithFeedback)
        } else {
            planLibrary
        }
    }
}
