package it.unibo.jakta.agents.bdi.plangeneration.manager.generation.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.intentions.Intention
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.plangeneration.PlanGenerationResult
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary

class GenerationStateManager(
    private val loggingConfig: LoggingConfig?,
    private val logger: KLogger?,
) {

    fun initializeOrReuseGenerationState(
        context: AgentContext,
        intention: Intention,
        plan: PartialPlan,
        generationStrategy: GenerationStrategy,
        genGoal: Generate,
        environment: Environment,
    ): GenerationState {
        val generationProcess = context.generationProcesses[plan.id]
        val updatedPlanLibrary = createCleanPlanLibrary(context, intention, plan)

        return generationProcess?.also {
            logger?.info { "Reusing generation process for goal: ${it.goal}" }
        } ?: generationStrategy.initializeGeneration(
            genGoal,
            plan.id,
            context.copy(planLibrary = updatedPlanLibrary),
            environment.externalActions.values.toList(),
            loggingConfig,
        ).also {
            logger?.info { "Creating new generation process for goal: ${formatter.format(genGoal.value)}" }
        }
    }

    fun createCleanPlanLibrary(
        context: AgentContext,
        intention: Intention,
        plan: PartialPlan,
    ): PlanLibrary =
        PlanLibrary.of(
            context.planLibrary
                .removePlan(intention.currentPlan())
                .plans
                .filterNot { it is PartialPlan && it.parentPlanID != plan.id },
        )

    fun checkIfGenerationFinished(
        planGenResult: PlanGenerationResult,
        plan: PartialPlan,
        generationStrategy: GenerationStrategy,
        context: AgentContext,
    ): PlanGenerationResult =
        if (planGenResult.generationState.isGenerationFinished) {
            val planToUpdate = context.planLibrary.plans
                .filterIsInstance<PartialPlan>()
                .firstOrNull { it.id == plan.id }

            planToUpdate?.let {
                val updatedState = generationStrategy.checkGenerationEnded(
                    planGenResult.generationState.goal,
                    planGenResult.generationState,
                    context.beliefBase,
                    planToUpdate,
                    context.planLibrary.plans.filter { childPlan ->
                        childPlan is PartialPlan &&
                            childPlan.parentPlanID == plan.id &&
                            childPlan.id != plan.id
                    },
                )
                planGenResult.copy(generationState = updatedState)
            } ?: planGenResult.also {
                logger?.error { "Could not find the updated version of the generating plan" }
            }
        } else {
            planGenResult
        }
}
