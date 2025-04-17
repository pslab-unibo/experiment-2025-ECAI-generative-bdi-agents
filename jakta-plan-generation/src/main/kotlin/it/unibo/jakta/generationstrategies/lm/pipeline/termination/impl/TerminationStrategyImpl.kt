package it.unibo.jakta.generationstrategies.lm.pipeline.termination.impl

import it.unibo.jakta.agents.bdi.Jakta.formatter
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenerationCompleted
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GenericGenerationFailure
import it.unibo.jakta.agents.bdi.plangeneration.feedback.GuardFlatteningVisitor.Companion.flattenAnd
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.feedback.FeedbackProvider
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.BinaryQuestionHandler
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.TerminationStrategy
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy

class TerminationStrategyImpl(
    override val feedbackProvider: FeedbackProvider,
    override val binaryQuestionHandler: BinaryQuestionHandler,
) : TerminationStrategy {

    /**
     * By default, a symbolically encoded success condition is extracted from the declarative goal name.
     * The termination is handled by solving it using the current belief base.
     * If the solving fails, then the LM might be queried up to two times.
     * The first time it is asked whether the declarative goal is completed.
     * If the response is affirmative, the generation process is considered successfully completed.
     * Otherwise, the LM is asked if it is possible to complete the given declarative goal, given the current state.
     * If the response is negative, then the generation process terminates.
     * Otherwise, the process keeps on going and the LM is asked to generate a response that this time is parsable.
     */
    override fun checkGenerationEnded(
        goalToAchieve: Generate,
        beliefBase: BeliefBase,
        generationStrategy: LMGenerationStrategy,
        generationState: LMGenerationState,
        generatedPlan: PartialPlan,
        additionalPlans: List<Plan>,
    ): GenerationState {
        val goals = goalToAchieve.value.flattenAnd()
        val res = goals.map { beliefBase.solve(it) }
        return if (res.any { !it.isYes }) {
            val confirmationState = binaryQuestionHandler.askConfirmation(
                generatedPlan,
                generationState,
                generationStrategy,
            )
            if (confirmationState.isGenerationEndConfirmed) {
                provideFeedback(confirmationState, generatedPlan, additionalPlans)
            } else {
                val possibilityState = binaryQuestionHandler.askPossibility(
                    generatedPlan,
                    confirmationState,
                    generationStrategy,
                )
                if (possibilityState.isGenerationEndConfirmed) {
                    val feedback = GenericGenerationFailure(
                        "Cannot generate a solution for ${formatter.format(goalToAchieve.value)}.",
                    )
                    feedbackProvider.provideGenerationFeedback(possibilityState, feedback)
                } else {
                    binaryQuestionHandler.setupClarification(possibilityState)
                }
            }
        } else {
            provideFeedback(generationState, generatedPlan, additionalPlans).copy(
                isGenerationEndConfirmed = true,
            )
        }
    }

    private fun provideFeedback(
        generationState: LMGenerationState,
        generatedPlan: PartialPlan,
        additionalPlans: List<Plan>,
    ): GenerationState {
        val feedback = GenerationCompleted(generatedPlan, additionalPlans)
        return feedbackProvider.provideGenerationFeedback(generationState, feedback)
    }
}
