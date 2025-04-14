package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.plangeneration.feedback.InapplicablePlan
import it.unibo.jakta.agents.bdi.plangeneration.feedback.PlanNotFound
import it.unibo.jakta.agents.bdi.plangeneration.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.UnavailablePlanStrategy
import it.unibo.jakta.agents.bdi.plans.Plan

class UnavailablePlanStrategyImpl(
    override val invalidationStrategy: InvalidationStrategy,
    override val logger: KLogger?,
) : UnavailablePlanStrategy {

    override fun handleUnavailablePlans(
        selectedEvent: Event,
        relevantPlans: List<Plan>,
        isApplicablePlansEmpty: Boolean,
        intention: DeclarativeIntention,
        context: AgentContext,
    ): ExecutionResult? =
        when {
            relevantPlans.isEmpty() -> handlePlanNotFound(intention, context)
            selectedEvent.trigger is AchievementGoalFailure -> null
            selectedEvent.trigger is TestGoalFailure -> null
            isApplicablePlansEmpty -> handleFailedPlanPreconditions(selectedEvent, relevantPlans, intention, context)
            else -> null // not expected since either relevantPlans and/or applicablePlans should be empty
        }

    /**
     * Invalidate the failing step in the generating plan.
     */
    fun handlePlanNotFound(
        intention: DeclarativeIntention,
        context: AgentContext,
    ): ExecutionResult =
        invalidationStrategy.invalidate(intention, context).copy(
            feedback = PlanNotFound(intention.currentPlan().trigger),
        )

    /**
     * Invalidate both the step in the generating plan and the failing plan.
     */
    fun handleFailedPlanPreconditions(
        selectedEvent: Event,
        relevantPlans: List<Plan>,
        intention: DeclarativeIntention,
        context: AgentContext,
    ): ExecutionResult =
        invalidationStrategy.invalidate(intention, context).copy(
            feedback = InapplicablePlan(
                relevantPlans
                    .map { it.checkApplicability(selectedEvent, context.beliefBase) }
                    .filter { it.error == null && it.trigger != null && it.guards != null },
            ),
        )
}
