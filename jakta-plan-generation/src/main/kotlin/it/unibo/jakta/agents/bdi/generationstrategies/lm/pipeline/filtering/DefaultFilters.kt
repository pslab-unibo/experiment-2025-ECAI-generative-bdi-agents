package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering

import it.unibo.jakta.agents.bdi.engine.actions.InternalActions
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.engine.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.engine.generation.manager.impl.GenerationPlanBuilder
import it.unibo.jakta.agents.bdi.engine.generation.manager.impl.GenerationPlanBuilder.createNewTriggerFromGoal
import it.unibo.jakta.agents.bdi.engine.generation.manager.impl.GenerationPlanBuilder.getFailureTrigger
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

object DefaultFilters {
    /**
     * Filters out special generation plans from the context that are used when no plan is available,
     * preventing them from being shown to the language model.
     */
    val metaPlanFilter =
        ContextFilter { extendedContext ->
            extendedContext.createNewTriggerFromGoal()?.let { trigger ->
                val generationPlanId = GenerationPlanBuilder.getGenerationPlanID(trigger)
                extendedContext.copyWithFilteredPlans { plan ->
                    plan.id != generationPlanId &&
                        plan.trigger !is TestGoalFailure &&
                        plan.trigger !is AchievementGoalFailure
                }
            } ?: extendedContext
        }

    val printActionFilter =
        ContextFilter { extendedContext ->
            extendedContext.copy(
                context =
                    extendedContext.context.copy(
                        internalActions =
                            extendedContext.context.internalActions
                                .minus(InternalActions.Print.signature.name),
                    ),
            )
        }

    private fun ExtendedAgentContext.createNewTriggerFromGoal() =
        createNewTriggerFromGoal(initialGoal.goal)?.let { getFailureTrigger(it) }

    private fun ExtendedAgentContext.copyWithFilteredPlans(filter: (Plan) -> Boolean) =
        ExtendedAgentContext(
            initialGoal,
            context.copy(
                planLibrary =
                    PlanLibrary.of(
                        context.planLibrary.plans.filterNot(filter),
                    ),
            ),
            externalActions,
        )
}
