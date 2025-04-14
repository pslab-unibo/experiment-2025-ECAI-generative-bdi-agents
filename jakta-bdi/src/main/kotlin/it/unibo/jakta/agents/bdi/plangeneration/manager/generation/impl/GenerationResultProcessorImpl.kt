package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultProcessor
import it.unibo.jakta.agents.bdi.plans.PlanID

class GenerationResultProcessorImpl(
    override val logger: KLogger?,
) : GenerationResultProcessor {

    private val intentionUpdater = IntentionUpdater(logger)
    private val requestManager = GenerationRequestManager(logger)
    private val planLibraryManager = PlanLibraryManager(logger)
    private val eventManager = EventManager(logger)

    override fun processResult(
        context: AgentContext,
        intention: DeclarativeIntention,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        val updatedIntention = intentionUpdater.updateIntention(
            intention,
            planID,
            planGenResult,
        )

        val updatedGenerationRequests = requestManager.updateGenerationRequests(
            context,
            planID,
            planGenResult,
        )

        val updatedPlanLibrary = planLibraryManager.updatePlanLibrary(
            context,
            planID,
            planGenResult,
        )

        val updatedEvents = eventManager.updateEvents(
            context,
            planID,
            updatedIntention,
            planGenResult,
        )

        return ExecutionResult(
            context.copy(
                intentions = context.intentions.updateIntention(updatedIntention),
                generationRequests = updatedGenerationRequests,
                planLibrary = updatedPlanLibrary,
                events = updatedEvents,
            ),
        )
    }
}
