package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.copy

class PlanLibraryManager(private val logger: KLogger?) {

    fun updatePlanLibrary(
        context: AgentContext,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): PlanLibrary =
        if (!planGenResult.generationState.isGenerationEndConfirmed) {
            updatePlanLibraryWithGeneratingPlan(context, planID, planGenResult)
        } else {
            finalizePlanLibrary(context, planID)
        }

    private fun updatePlanLibraryWithGeneratingPlan(
        context: AgentContext,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): PlanLibrary {
        // Remove the currently generating plan if an updated version is available.
        val generatedPlans = planGenResult.generatedPlanLibrary
        val planToUpdate = generatedPlans.plans.firstOrNull { it.id == planID }

        val filteredPlans = if (planToUpdate != null) {
            context.planLibrary.plans.filter { it.id != planID }
        } else {
            context.planLibrary.plans
        }

        val nGeneratedPlans = if (planToUpdate != null) {
            generatedPlans.plans.size - 1
        } else {
            generatedPlans.plans.size
        }

        when (nGeneratedPlans) {
            0 -> logger?.info { "No additional plans generated" }
            1 -> logger?.info { "Added one generated plan" }
            else -> logger?.info { "Added $nGeneratedPlans generated plans" }
        }

        return PlanLibrary.of(filteredPlans) + generatedPlans
    }

    private fun finalizePlanLibrary(context: AgentContext, planID: PlanID): PlanLibrary {
        // Find the plan that was being generated.
        val planToUpdate = context.planLibrary.plans
            .filterIsInstance<PartialPlan>()
            .firstOrNull { it.id == planID }

        if (planToUpdate == null) {
            return context.planLibrary
        }

        // Remove [Generate] goals since generation is complete.
        val updatedGoals = planToUpdate.goals.filterNot { it is Generate }
        val updatedPlan = planToUpdate.copy(goals = updatedGoals)

        // Convert suitable partial plans to literate plans.
        val updatedPlanLibrary = context.planLibrary
            .updatePlan(updatedPlan)
            .plans
            .filterIsInstance<PartialPlan>()
            .map { plan ->
                if (plan.goals.any { it is Generate || it is TrackGoalExecution }) {
                    plan // Keep as partial plan if it has a [Generate] or a [TrackGoal]
                } else {
                    convertToLiteratePlan(plan)
                }
            }
            .filterNot { it is PartialPlan && it.parentPlanID == planID } // Remove child plans

        return PlanLibrary.of(
            context.planLibrary.plans.filter { it !is PartialPlan } + updatedPlanLibrary,
        ).also {
            logger?.info { "Converting all eligible partial plans to completed plans" }
            logger?.info { "Removing all remaining partial plans created by this generation process" }
        }
    }

    private fun convertToLiteratePlan(plan: PartialPlan): LiteratePlan =
        LiteratePlan.of(
            id = plan.id,
            trigger = plan.trigger,
            guard = plan.guard,
            goals = plan.goals,
            literateTrigger = plan.literateTrigger,
            literateGuard = plan.literateGuard,
            literateGoals = plan.literateGoals,
        )
}
