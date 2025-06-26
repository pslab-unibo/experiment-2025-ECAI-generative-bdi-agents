package it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.impl

import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionResult
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.GenerationResultBuilder
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.updaters.GenerationProcessUpdater
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.updaters.PlanLibraryUpdater
import it.unibo.jakta.agents.bdi.engine.generation.manager.plangeneration.updaters.TemplateUpdater
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.intentions.Intention
import it.unibo.jakta.agents.bdi.engine.logging.loggers.AgentLogger

internal class GenerationResultBuilderImpl(
    override val logger: AgentLogger?,
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
