package it.unibo.jakta.generationstrategies.lm.pipeline.generation.impl

import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanFactory
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.copy
import it.unibo.jakta.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.LMPlanGenerationResult
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator.Companion.getErrorMsgFromParsingFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ResponseParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.CompositeParserSuccess
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.EmptyResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.NewPlan
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.NewStep
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.RequestFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy

class MultiStepGenerator(
    override val requestHandler: RequestHandler,
    override val responseParser: ResponseParser,
) : LMPlanGenerator {
    override fun handleParserResults(
        generatingPlan: PartialPlan,
        generationStrategy: LMGenerationStrategy,
        generationResult: ParserResult,
        generationState: LMGenerationState,
    ): GenerationResult =
        when (generationResult) {
            is CompositeParserSuccess -> {
                handleCompositeResult(
                    generationStrategy,
                    generationState,
                    generatingPlan,
                    generationResult,
                )
            }
            is RequestFailure -> handleRequestFailure(
                generationState,
                generationResult,
            )
            is NewStep -> {
                val res = handleNewStep(generatingPlan, generationResult)
                LMPlanGenerationResult(generationState, res)
            }
            is NewPlan -> {
                val res = handleNewPlan(generationStrategy, generationState, generationResult)
                if (res != null) {
                    LMPlanGenerationResult(generationState, res)
                } else {
                    LMGenerationFailure(generationState, "Unknown parser response")
                }
            }
            is EmptyResponse -> handleEmptyResponse(generationState)
            is PlanGenerationParserFailure -> {
                val errorMsg = getErrorMsgFromParsingFailure(generationResult)
                handleParsingFailure(errorMsg, generationState)
            }
            else -> LMGenerationFailure(generationState, "Unknown parser response")
        }

    private fun handleCompositeResult(
        generationStrategy: LMGenerationStrategy,
        generationState: LMGenerationState,
        generatingPlan: PartialPlan,
        res: CompositeParserSuccess,
    ): GenerationResult {
        val updatedGeneratingPlan = res.newStep?.let { handleNewStep(generatingPlan, it) }
        val newPlans = res.newPlans?.let { plans ->
            plans.mapNotNull { handleNewPlan(generationStrategy, generationState, it) }
        }
        val planLibrary = when {
            updatedGeneratingPlan != null && newPlans != null -> PlanLibrary.of(newPlans + updatedGeneratingPlan)
            updatedGeneratingPlan != null -> PlanLibrary.of(updatedGeneratingPlan)
            newPlans != null -> PlanLibrary.of(newPlans)
            else -> null
        }
        return planLibrary?.let { LMPlanGenerationResult(generationState, it) }
            ?: LMGenerationFailure(generationState, "A CompositeParserSuccess cannot have both fields as null")
    }

    /*
     * Plans suggested by a LLM are considered partial and composed only
     * of goals of type TrackGoalExecution since they need to be validated.
     */
    private fun handleNewPlan(
        generationStrategy: LMGenerationStrategy,
        generationState: LMGenerationState,
        res: NewPlan,
    ): Plan? {
        val plan = res.plan
        return if (plan.goals.isNotEmpty()) {
            val goals = plan.goals.map { TrackGoalExecution.of(it) }
            PlanFactory(
                trigger = plan.trigger,
                goals = goals,
                guard = plan.guard,
                generationStrategy = generationStrategy,
                parentPlanID = generationState.rootPlanID,
                literateTrigger = plan.literateTrigger,
                literateGuard = plan.literateGuard,
                literateGoals = plan.literateGoals,
            ).build()
        } else {
            null
        }
    }

    /*
     * Wrap the suggested new step in a [TrackGoalExecution] goal
     */
    private fun handleNewStep(
        generatingPlan: PartialPlan,
        res: NewStep,
    ): Plan {
        /*
         * Add the generated step in a [TrackGoalExecution]
         * Prepend the track goal to the [Generate] goal
         */
        val generateGoal = generatingPlan.goals.first { it is Generate }
        val trackGoal = TrackGoalExecution.of(res.goal)
        val updatedGoals = generatingPlan.goals.flatMap { goal ->
            if (goal == generateGoal) {
                listOf(trackGoal, generateGoal)
            } else {
                listOf(goal)
            }
        }

        return generatingPlan.copy(goals = updatedGoals)
    }

    /**
     * This might be the final plan; no further steps or new plans are expected.
     *
     * No [TrackGoalExecution]s or more than one [Generate] goal are expected.
     */
    private fun handleEmptyResponse(
        generationState: LMGenerationState,
    ): LMPlanGenerationResult =
        LMPlanGenerationResult(generationState.copy(isGenerationFinished = true))

    private fun handleRequestFailure(
        generationState: LMGenerationState,
        res: RequestFailure,
    ): GenerationResult =
        LMGenerationFailure(
            generationState = generationState.copy(
                failedGenerationProcess = generationState.failedGenerationProcess + 1,
            ),
            errorMsg = res.rawContent,
        )
}
