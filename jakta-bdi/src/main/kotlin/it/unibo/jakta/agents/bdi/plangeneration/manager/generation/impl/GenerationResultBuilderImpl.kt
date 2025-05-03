package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.GenerationResultBuilder
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters.GenerationProcessUpdater
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters.PlanLibraryUpdater
import it.unibo.jakta.agents.bdi.plangeneration.manager.generation.updaters.TemplateUpdater

class GenerationResultBuilderImpl(
    override val logger: KLogger?,
) : GenerationResultBuilder {
    private val planLibraryUpdater = PlanLibraryUpdater(logger)
    private val templateUpdater = TemplateUpdater(logger)
    private val generationProcessUpdater = GenerationProcessUpdater(logger)

    override fun buildResult(
        genGoal: GeneratePlan,
        context: AgentContext,
        intention: Intention,
        planGenResult: PlanGenerationResult,
    ): ExecutionResult {
        val updatedPlanLibrary = planLibraryUpdater.update(context, planGenResult)
        val updatedAdmissibleGoals = templateUpdater.updateAdmissibleGoals(context, planGenResult)
        val updatedAdmissibleBeliefs = templateUpdater.updateAdmissibleBeliefs(context, planGenResult)
        val updatedGenProcesses = generationProcessUpdater.update(context, planGenResult)

        return ExecutionResult(
            context.copy(
                intentions = context.intentions.updateIntention(intention.pop()),
                planLibrary = updatedPlanLibrary,
                admissibleBeliefs = updatedAdmissibleBeliefs,
                admissibleGoals = updatedAdmissibleGoals,
                generationProcesses = updatedGenProcesses,
            ),
        )
    }
}
