package it.unibo.jakta.generationstrategies.lm.strategy

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.Generate
import it.unibo.jakta.agents.bdi.logging.LoggerFactory
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.feedback.ExecutionFeedback
import it.unibo.jakta.agents.bdi.plans.PartialPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.feedback.FeedbackProvider
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.pipeline.termination.TerminationStrategy
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.configErrorMsg
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.logChatMessage
import java.util.UUID

class LMGenerationStrategyImpl(
    override val generator: LMPlanGenerator,
    override val promptBuilder: PromptBuilder,
    override val feedbackProvider: FeedbackProvider,
    override val terminationStrategy: TerminationStrategy,
) : LMGenerationStrategy {

    override fun requestBlockingGeneration(
        generatingPlan: PartialPlan,
        generationState: GenerationState,
    ): GenerationResult {
        val lmState = generationState as? LMGenerationState
        return if (lmState != null) {
            generator.generate(generatingPlan, this@LMGenerationStrategyImpl, lmState)
        } else {
            LMGenerationFailure(generationState, configErrorMsg(generationState))
        }
    }

    override fun initializeGeneration(
        goal: Generate,
        rootPlanID: PlanID,
        context: AgentContext,
        externalActions: List<ExternalAction>,
        loggingConfig: LoggingConfig?,
    ): GenerationState {
        val internalActions = context.internalActions.values.toList()

        val history = promptBuilder.buildPrompt(
            internalActions,
            externalActions,
            context.beliefBase,
            context.planLibrary.plans,
            goal.literateValue,
        )

        val logger = loggingConfig?.let { createConversationLogger(it) }

        return LMGenerationState(
            goal = goal,
            logger = logger,
            rootPlanID = rootPlanID,
            chatHistory = history,
        ).also {
            history.forEach { msg -> it.logger?.logChatMessage(msg) }
        }
    }

    override fun provideGenerationFeedback(
        generationState: GenerationState,
        executionFeedback: ExecutionFeedback,
    ): GenerationState =
        feedbackProvider.provideGenerationFeedback(
            generationState,
            executionFeedback,
        )

    override fun checkGenerationEnded(
        goal: Generate,
        generationState: GenerationState,
        beliefBase: BeliefBase,
        generatedPlan: PartialPlan,
        additionalPlans: List<Plan>,
    ): GenerationState {
        val lmState = generationState as? LMGenerationState
        return if (lmState != null) {
            terminationStrategy.checkGenerationEnded(
                goal,
                beliefBase,
                this,
                lmState,
                generatedPlan,
                additionalPlans,
            )
        } else {
            generationState.logger?.error { configErrorMsg(generationState) }
            generationState
        }
    }

    override fun toString(): String {
        return "LMGenerationStrategyImpl(" +
            "planGenerator=$generator, " +
            "promptBuilder=$promptBuilder, " +
            "feedbackProvider=$feedbackProvider, " +
            "terminationStrategy=$terminationStrategy)"
    }

    companion object {
        private fun createConversationLogger(loggingConfig: LoggingConfig): KLogger {
            val loggerName = UUID.randomUUID().toString()
            val agentLogPath = loggingConfig.logDir
            val loggerConfig = loggingConfig.copy(logDir = "$agentLogPath/chat")
            return LoggerFactory.createLogger(loggerConfig, loggerName)
        }
    }
}
