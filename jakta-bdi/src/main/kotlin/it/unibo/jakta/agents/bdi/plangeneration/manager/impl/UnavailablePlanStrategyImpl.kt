package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.effects.IntentionChange
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.context.ContextUpdate.REMOVAL
import it.unibo.jakta.agents.bdi.events.AchievementGoalFailure
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.TestGoalFailure
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.feedback.InapplicablePlan
import it.unibo.jakta.agents.bdi.plangeneration.feedback.PlanNotFound
import it.unibo.jakta.agents.bdi.plangeneration.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.UnavailablePlanStrategy
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan

class UnavailablePlanStrategyImpl(
    override val invalidationStrategy: InvalidationStrategy,
    override val logger: KLogger?,
) : UnavailablePlanStrategy {

    override fun handleUnavailablePlans(
        selectedEvent: Event,
        relevantPlans: List<Plan>,
        isApplicablePlansEmpty: Boolean,
        context: AgentContext,
        generationStrategy: GenerationStrategy?,
    ): ExecutionResult {
        val intention = selectedEvent.intention
        return when {
            relevantPlans.isEmpty() -> handlePlanNotFound(
                selectedEvent,
                intention,
                context,
                generationStrategy,
            )

            selectedEvent.trigger is AchievementGoalFailure -> handleIntentionFailure(selectedEvent, context)
            selectedEvent.trigger is TestGoalFailure -> handleIntentionFailure(selectedEvent, context)
            isApplicablePlansEmpty -> handleFailedPlanPreconditions(selectedEvent, relevantPlans, intention, context)
            else -> handleIntentionFailure(
                selectedEvent,
                context,
            ) // not expected since either relevantPlans and/or applicablePlans should be empty
        }
    }

    private fun handleIntentionFailure(
        selectedEvent: Event,
        context: AgentContext,
    ): ExecutionResult =
        if (selectedEvent.isInternal()) {
            val intentionToRemove = selectedEvent.intention!!
            val updatedIntentions = context.intentions.deleteIntention(intentionToRemove.id)
            ExecutionResult(newAgentContext = context.copy(intentions = updatedIntentions)).also {
                logger?.implementation(IntentionChange(intentionToRemove, REMOVAL))
            }
        } else {
            ExecutionResult(newAgentContext = context)
        }

    /**
     * Invalidate the failing step in the generating plan.
     */
    fun handlePlanNotFound(
        selectedEvent: Event,
        intention: Intention?,
        context: AgentContext,
        generationStrategy: GenerationStrategy?,
    ): ExecutionResult {
        val baseResult = if (intention != null && intention is DeclarativeIntention) {
            invalidationStrategy.invalidate(intention, context)
        } else {
            handleIntentionFailure(selectedEvent, context)
        }

        val resultWithNewEvent = if (selectedEvent.trigger is AchievementGoalInvocation ||
            selectedEvent.trigger is TestGoalInvocation
        ) {
            val newPlan = PartialPlan.of(
                trigger = selectedEvent.trigger,
                goals = listOf(Generate.of(selectedEvent.trigger.value)),
                generationStrategy = generationStrategy,
            )
            val newIntention = Intention.of(newPlan)
            val newEvent = Event.of(selectedEvent.trigger, newIntention)
            baseResult.copy(
                newAgentContext = baseResult.newAgentContext.copy(
                    events = baseResult.newAgentContext.events.plus(newEvent).minus(selectedEvent),
                    planLibrary = baseResult.newAgentContext.planLibrary.addPlan(newPlan),
                ),
            )
        } else {
            baseResult
        }

        return resultWithNewEvent.copy(
            feedback = PlanNotFound(selectedEvent.trigger),
        )
    }

    /**
     * Invalidate both the step in the generating plan and the failing plan.
     */
    fun handleFailedPlanPreconditions(
        selectedEvent: Event,
        relevantPlans: List<Plan>,
        intention: Intention?,
        context: AgentContext,
    ): ExecutionResult = if (intention != null && intention is DeclarativeIntention) {
        invalidationStrategy.invalidate(intention, context)
    } else {
        handleIntentionFailure(selectedEvent, context)
    }.copy(
        feedback = InapplicablePlan(
            relevantPlans
                .map { it.checkApplicability(selectedEvent, context.beliefBase) }
                .filter { it.error == null && it.trigger != null && it.guards != null },
        ),
    )
}
