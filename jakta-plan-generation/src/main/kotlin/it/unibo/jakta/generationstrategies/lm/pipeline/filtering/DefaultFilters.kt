package it.unibo.jakta.generationstrategies.lm.pipeline.filtering

import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationPlanBuilder
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationPlanBuilder.createNewTriggerFromGoal
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationPlanBuilder.getFailureTrigger
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

object DefaultFilters {
    /*
     * Does not show to the LM the special plans that make the generation start when a plan is not available
     * and the special beliefs used to mark when there are missing plans for a goal.
     */
    val defaultFilter = object : ContextFilter {
        override fun filter(extendedContext: ExtendedAgentContext): ExtendedAgentContext {
            val initialGoal = extendedContext.initialGoal
            val context = extendedContext.context
            val externalActions = extendedContext.externalActions
            val trigger = createNewTriggerFromGoal(initialGoal.goal)?.let { getFailureTrigger(it) }
            return if (trigger != null) {
                val id = GenerationPlanBuilder.getGenerationPlanID(trigger)
                val filteredContext = context.copy(
                    planLibrary = PlanLibrary.of(
                        context.planLibrary.plans
                            .filterNot { it.id == id }
                            .filterNot { it.trigger is TestGoalFailure || it.trigger is AchievementGoalFailure }
                            .distinctBy { it.trigger },
                    ),
                )
                ExtendedAgentContext(initialGoal, filteredContext, externalActions)
            } else {
                ExtendedAgentContext(initialGoal, context, externalActions)
            }
        }
    }
}
