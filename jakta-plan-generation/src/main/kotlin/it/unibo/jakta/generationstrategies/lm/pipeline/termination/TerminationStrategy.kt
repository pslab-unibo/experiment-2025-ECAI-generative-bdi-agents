package it.unibo.jakta.generationstrategies.lm.pipeline.termination

import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.feedback.FeedbackProvider
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.impl.TerminationStrategyImpl
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy

interface TerminationStrategy {
    val feedbackProvider: FeedbackProvider
    val binaryQuestionHandler: BinaryQuestionHandler

    /**
     * Try to verify if the goals are achieved symbolically.
     * If the verification fails, ask for clarifications the LLM.
     * If this cannot be done, then just ask the LLM for confirmation.
     * If the confirmation is not given, ask for clarifications the LLM.
     * Otherwise, consider the generation ended and report the completed plan.
     */
    fun checkGenerationEnded(
        goalsToAchieve: Generate,
        beliefBase: BeliefBase,
        generationStrategy: LMGenerationStrategy,
        generationState: LMGenerationState,
        generatedPlan: PartialPlan,
        additionalPlans: List<Plan>,
    ): GenerationState

    companion object {
        fun of(
            feedbackProvider: FeedbackProvider,
            binaryQuestionHandler: BinaryQuestionHandler,
        ) = TerminationStrategyImpl(feedbackProvider, binaryQuestionHandler)
    }
}
