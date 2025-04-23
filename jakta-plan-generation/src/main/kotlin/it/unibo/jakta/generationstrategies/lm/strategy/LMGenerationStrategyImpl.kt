package it.unibo.jakta.generationstrategies.lm.strategy

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.logging.LoggerFactory
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.configErrorMsg
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.logChatMessage
import kotlinx.coroutines.runBlocking
import java.util.UUID

class LMGenerationStrategyImpl(
    override val generator: LMPlanGenerator,
    override val promptBuilder: PromptBuilder,
) : LMGenerationStrategy {

    override fun requestBlockingGeneration(
        generationStrategy: GenerationStrategy,
        generationState: GenerationState,
    ): GenerationResult = runBlocking {
        val lmState = generationState as? LMGenerationState
        return@runBlocking if (lmState != null) {
            generator.generate(this@LMGenerationStrategyImpl, lmState)
        } else {
            LMGenerationFailure(generationState, configErrorMsg(generationState))
        }
    }

    override fun initializeGeneration(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
        loggingConfig: LoggingConfig?,
    ): GenerationState {
        val history = promptBuilder.buildPrompt(initialGoal, context, externalActions)
        val logger = loggingConfig?.let { createConversationLogger(it) }

        return LMGenerationState(
            goal = initialGoal,
            logger = logger,
            chatHistory = listOf(history),
        ).also {
            logger?.logChatMessage(history)
        }
    }

    override fun toString(): String {
        return "LMGenerationStrategyImpl(" +
            "planGenerator=$generator, " +
            "promptBuilder=$promptBuilder)"
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
