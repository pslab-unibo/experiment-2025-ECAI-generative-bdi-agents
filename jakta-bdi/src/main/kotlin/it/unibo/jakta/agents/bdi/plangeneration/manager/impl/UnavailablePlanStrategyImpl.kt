package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.Event
import it.unibo.jakta.agents.bdi.events.TestGoalInvocation
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.NegativeFeedback
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.NegativeFeedback.InapplicablePlan
import it.unibo.jakta.agents.bdi.executionstrategies.feedback.NegativeFeedback.PlanNotFound
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.UnavailablePlanStrategy
import it.unibo.jakta.agents.bdi.plangeneration.manager.impl.GenerationPlanBuilder.getGenerationPlan
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.tuprolog.core.Struct

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
        return when {
            relevantPlans.isEmpty() -> handlePlanNotFound(selectedEvent, context, generationStrategy)
            isApplicablePlansEmpty ->
                handleFailedPlanPreconditions(selectedEvent, relevantPlans, context)
            // not expected since either relevantPlans and/or applicablePlans should be empty
            else -> ExecutionResult(newAgentContext = context)
        }
    }

    private fun handlePlanNotFound(
        selectedEvent: Event,
        context: AgentContext,
        generationStrategy: GenerationStrategy?,
    ): ExecutionResult {
        val initialGoal = selectedEvent.trigger
        val newPlan = if (generationStrategy != null &&
            (initialGoal is AchievementGoalInvocation || initialGoal is TestGoalInvocation)
        ) {
            getGenerationPlan(initialGoal)
        } else {
            null
        }
        val feedback = PlanNotFound(initialGoal)

        return createResult(initialGoal, context, feedback, newPlan)
    }

    private fun handleFailedPlanPreconditions(
        selectedEvent: Event,
        relevantPlans: List<Plan>,
        context: AgentContext,
    ): ExecutionResult {
        val initialGoal = selectedEvent.trigger
        val feedback = InapplicablePlan(
            relevantPlans
                .map { it.checkApplicability(selectedEvent, context.beliefBase) }
                .filter { it.error == null && it.trigger != null && it.guards != null }
                .also {
                    it.forEach { plan ->
                        logger?.warn { "Plan ${plan.trigger?.value} is not applicable" }
                        plan.guards?.forEach { guard ->
                            if (!guard.value) logger?.warn { "Failing guard ${guard.key}" }
                        }
                    }
                },
        )

        return createResult(initialGoal, context, feedback)
    }

    private fun createResult(
        failureTrigger: Trigger,
        context: AgentContext,
        feedback: NegativeFeedback,
        newPlan: PartialPlan? = null,
    ): ExecutionResult {
        return if (newPlan != null) {
            val newBelief = Belief.fromSelfSource(Struct.of("missing_plan_for", failureTrigger.value))
            val newEvent = Event.ofAchievementGoalFailure(failureTrigger.value)
            ExecutionResult(
                newAgentContext = context.copy(
                    planLibrary = context.planLibrary.addPlan(newPlan),
                    beliefBase = context.beliefBase.add(newBelief).updatedBeliefBase,
                    events = context.events + newEvent,
                ),
                feedback = feedback,
            ).also {
                logger?.implementation(EventChange(newEvent, ADDITION))
            }
        } else {
            ExecutionResult(
                newAgentContext = context,
                feedback = feedback,
            )
        }
    }
}
