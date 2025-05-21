package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation.impl

import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.goals.TrackGoalExecution
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.engine.plans.PartialPlan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.EmptyResponse
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.GenericParserFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.NetworkRequestFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserSuccess.NewPlan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserSuccess.NewResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.request.RequestHandler
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy
import kotlin.collections.isNotEmpty
import kotlin.collections.map

internal class LMPlanGeneratorImpl(
    override val requestHandler: RequestHandler,
    override val responseParser: Parser,
) : LMPlanGenerator {
    override fun handleParserResults(
        generationStrategy: LMGenerationStrategy,
        generationResult: ParserResult,
        generationState: LMGenerationState,
    ): GenerationResult =
        when (generationResult) {
            is NewResult -> handleNewResult(generationState, generationResult)
            is NetworkRequestFailure -> handleRequestFailure(generationState, generationResult)
            is EmptyResponse -> handleEmptyResponse(generationState)
            is GenericParserFailure -> handleParsingFailure(generationResult, generationState)
        }

    private fun handleNewResult(
        generationState: LMGenerationState,
        res: NewResult,
    ): GenerationResult {
        val newPlans = res.plans.let { plans -> plans.mapNotNull { handleNewPlan(generationState.goal, it) } }
        return LMGenerationResult(generationState, newPlans, res.admissibleGoals, res.admissibleBeliefs).also {
            generationState.logger?.info { "\n" + newPlans.joinToString("\n") }
        }
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
        res: NetworkRequestFailure,
    ): GenerationResult =
        LMGenerationFailure(
            generationState = generationState,
            errorMsg = res.rawContent,
        )
}
