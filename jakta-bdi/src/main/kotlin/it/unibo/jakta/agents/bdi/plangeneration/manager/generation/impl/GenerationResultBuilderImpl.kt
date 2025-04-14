package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.IntentionPool
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultBuilder
import it.unibo.jakta.agents.bdi.plans.PlanID

class GenerationResultBuilderImpl(
    override val logger: KLogger?,
) : GenerationResultBuilder {

    private val intentionManager = IntentionManager(logger)
    private val requestManager = GenerationProcessManager(logger)
    private val planLibraryManager = PlanLibraryManager(logger)
    private val eventManager = EventManager(logger)

    override fun buildResult(
        context: AgentContext,
        intention: DeclarativeIntention,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        val updatedIntention = intentionManager.updateIntention(
            intention,
            planID,
            planGenResult,
        )

        val updatedGenerationProcess = requestManager.updateGenerationProcess(
            context,
            updatedIntention,
            planID,
            planGenResult,
        )

        val updatedEvents = eventManager.updateEvents(
            context,
            planID,
            updatedIntention,
            planGenResult,
        )

        val updatedPlanLibrary = planLibraryManager.updatePlanLibrary(
            context,
            planID,
            planGenResult,
        )

        val updatedIntentionPool = if (planGenResult.generationState.isGenerationEndConfirmed) {
            // At the end no new events are created, the intention is directly reused.
            context.intentions.updateIntention(updatedIntention)
        } else {
            // A new event is sent to keep rescheduling the updated plan.
            IntentionPool.of(context.intentions - updatedIntention.id)
        }

        return ExecutionResult(
            context.copy(
                intentions = updatedIntentionPool,
                generationProcess = updatedGenerationProcess,
                events = updatedEvents,
                planLibrary = updatedPlanLibrary,
            ),
        )
    }
}
