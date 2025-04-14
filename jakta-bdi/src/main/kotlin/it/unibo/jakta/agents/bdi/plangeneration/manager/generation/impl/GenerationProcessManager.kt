package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.intentions.DeclarativeIntention
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.registry.GenerationProcessRegistry
import it.unibo.jakta.agents.bdi.plans.PlanID

class GenerationProcessManager(private val logger: KLogger?) {

    fun updateGenerationProcess(
        context: AgentContext,
        intention: Intention,
        planID: PlanID,
        planGenResult: PlanGenerationResult,
    ): GenerationProcessRegistry {
        return if (!planGenResult.generationState.isGenerationEndConfirmed) {
            // Continue tracking this request and update its state.
            context.generationProcesses.updateGenerationProcess(planID, planGenResult.generationState)
        } else {
            // Check if there is a parent generation process running and update it
            val parentGenProcessPlanID = if (intention is DeclarativeIntention) {
                intention.currentGeneratingPlan()
            } else {
                null
            }

            val achievedGoalsByTerminatedGenProcess = planGenResult.generationState.achievedGoalsHistory
            val genState = context.generationProcesses[parentGenProcessPlanID]

            val updatedState = genState?.copy(
                achievedGoalsHistory = genState.achievedGoalsHistory + achievedGoalsByTerminatedGenProcess,
            )

            // Since the generation ended, remove the request.
            logger?.info { "Deleted generation process of ${formatter.format(planID.trigger.value)}" }
            return if (parentGenProcessPlanID != null && updatedState != null) {
                logger?.info {
                    "Updated generation state of the parent generation process " +
                        formatter.format(parentGenProcessPlanID.trigger.value)
                }
                context.generationProcesses
                    .updateGenerationProcess(parentGenProcessPlanID, updatedState)
            } else {
                context.generationProcesses
            }.deleteGenerationProcess(planID)
        }
    }
}
