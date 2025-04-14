package it.unibo.jakta.agents.bdi.plangeneration.manager.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.plangeneration.manager.InvalidationStrategy
import it.unibo.jakta.agents.bdi.plans.ActivationRecord
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.copy

class InvalidationStrategyImpl(
    override val logger: KLogger?,
) : InvalidationStrategy {

    override fun invalidate(
        intention: DeclarativeIntention,
        context: AgentContext,
        isPotentialInfiniteRecursion: Boolean,
    ): ExecutionResult {
        return if (isPotentialInfiniteRecursion) {
            handleInfiniteRecursion(intention, context)
        } else {
            handleRegularFailure(intention, context)
        }
    }

    private fun handleInfiniteRecursion(
        intention: DeclarativeIntention,
        context: AgentContext,
    ): ExecutionResult {
        val generatingPlanID = intention.currentGeneratingPlan()

        val planToUpdate = context.planLibrary.plans
            .filterIsInstance<PartialPlan>()
            .firstOrNull { it.id == generatingPlanID }

        val updatedPlans = context.planLibrary.plans
            .filterNot {
                it is PartialPlan &&
                    it.parentPlanID == generatingPlanID &&
                    it.parentPlanID != it.id
            }
            .let { originalFilteredPlans ->
                planToUpdate?.let { generatingPlan ->
                    val updatedGoals = generatingPlan.goals.filterIsInstance<Generate>()
                    originalFilteredPlans + generatingPlan.copy(goals = updatedGoals)
                } ?: originalFilteredPlans
            }

        val updatedPlanLibrary = PlanLibrary.of(updatedPlans)
        return createExecutionResult(intention, context, updatedPlanLibrary)
    }

    private fun handleRegularFailure(
        intention: DeclarativeIntention,
        context: AgentContext,
    ): ExecutionResult {
        val failedPlanID = intention.currentPlan()
        val generatingPlanID = intention.currentGeneratingPlan()

        val planToUpdate = context.planLibrary.plans
            .filterIsInstance<PartialPlan>()
            .firstOrNull { it.id == generatingPlanID }
            ?.let { it.copy(goals = it.goals.dropWhile { it !is Generate }) }

        val updatedPlanLibrary = if (failedPlanID == generatingPlanID) {
            context.planLibrary
        } else {
            context.planLibrary.removePlan(failedPlanID) {
                // Avoid deleting user-provided or previously completed plans.
                it is PartialPlan
            }
        }.let { p ->
            planToUpdate?.let { p.updatePlan(it) } ?: p
        }

        return createExecutionResult(intention, context, updatedPlanLibrary)
    }

    private fun createExecutionResult(
        intention: DeclarativeIntention,
        context: AgentContext,
        updatedPlanLibrary: PlanLibrary,
    ): ExecutionResult {
        val updatedIntention = backtrack(intention)
        val updatedEvents = context.events.filterNot {
            it.intention != null && it.intention?.id == updatedIntention.id
        }

        return ExecutionResult(
            newAgentContext = context.copy(
                planLibrary = updatedPlanLibrary,
                intentions = context.intentions.updateIntention(updatedIntention),
                events = updatedEvents,
            ),
        )
    }

    /**
     * Remove all the record stacks till reaching the one with the root plan ID and
     * remove the track plan execution of the plan in the last eliminated record stack.
     * The resulting intention should have as next goal a [Generate].
     */
    fun backtrack(intention: DeclarativeIntention): DeclarativeIntention =
        if (intention.currentPlan() == intention.currentGeneratingPlan()) {
            val previousActivationRecord = intention.recordStack.first()
            val newActivationRecord = ActivationRecord.of(
                previousActivationRecord.goalQueue.dropWhile { it !is Generate },
                previousActivationRecord.plan,
            )
            val newRecordStack = intention.recordStack.drop(1) + newActivationRecord
            intention.copy(recordStack = newRecordStack)
        } else {
            backtrack(intention.copy(recordStack = intention.recordStack.drop(1)))
        }
}
