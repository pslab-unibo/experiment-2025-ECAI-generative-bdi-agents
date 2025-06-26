package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation.impl

import it.unibo.jakta.agents.bdi.engine.generation.GenerationResult
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.engine.plans.PartialPlan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestSuccess.NewPlan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.result.RequestSuccess.NewResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy
import kotlin.collections.isNotEmpty
import kotlin.collections.map

internal class LMPlanGeneratorImpl(
    override val requestHandler: RequestHandler,
    override val responseParser: Parser,
) : LMPlanGenerator {
    override fun handleRequestResult(
        generationStrategy: LMGenerationStrategy,
        requestResult: RequestResult,
        generationState: LMGenerationState,
    ): GenerationResult =
        when (requestResult) {
            is NewResult -> handleNewResult(generationState, requestResult)
            is RequestFailure.NetworkRequestFailure -> handleRequestFailure(generationState, requestResult)
            is ParserFailure.EmptyResponse -> handleEmptyResponse(generationState)
            is RequestFailure -> handleRequestFailure(requestResult, generationState)
            else -> handleEmptyResponse(generationState)
        }

    private fun handleNewResult(
        generationState: LMGenerationState,
        res: NewResult,
    ): GenerationResult {
        val newPlans = res.plans.let { plans -> plans.mapNotNull { handleNewPlan(generationState.goal, it) } }
        return LMGenerationResult(generationState, newPlans, res.admissibleGoals, res.admissibleBeliefs)
    }

    private fun handleNewPlan(
        initialGoal: GeneratePlan,
        res: NewPlan,
    ): PartialPlan? =
        if (res.goals.isNotEmpty()) {
            val goals = res.goals.map { TrackGoalExecution.of(it) }
            PartialPlan.of(
                trigger = res.trigger,
                goals = goals,
                guard = res.guard,
                parentGenerationGoal = initialGoal,
            )
        } else {
            null
        }

    private fun handleEmptyResponse(generationState: LMGenerationState): GenerationResult =
        LMGenerationFailure(
            generationState = generationState,
            errorMsg = "Empty response",
        )

    private fun handleRequestFailure(
        generationState: LMGenerationState,
        res: RequestFailure.NetworkRequestFailure,
    ): GenerationResult =
        LMGenerationFailure(
            generationState = generationState,
            errorMsg = res.rawContent,
        )
}
