package it.unibo.jakta.generationstrategies.lm.strategy

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.logging.LoggerFactory
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationConfig
import it.unibo.jakta.agents.bdi.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig.LMGenerationConfigContainer
import it.unibo.jakta.generationstrategies.lm.LMGenerationConfig.LMGenerationConfigUpdate
import it.unibo.jakta.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.configErrorMsg
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy.Companion.logChatMessage
import kotlinx.coroutines.runBlocking
import java.util.UUID

class LMGenerationStrategyImpl(
    override val generator: LMPlanGenerator,
    override val generationConfig: LMGenerationConfigContainer,
) : LMGenerationStrategy {

    override fun requestBlockingGeneration(
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
        val promptBuilder = generationConfig.promptBuilder
        val prompt = promptBuilder.build(
            initialGoal,
            context,
            externalActions,
            generationConfig.contextFilter,
            generationConfig.remarks,
        )
        val logger = loggingConfig?.let { createConversationLogger(it) }

        return LMGenerationState(
            goal = initialGoal,
            logger = logger,
            chatHistory = listOf(prompt),
        ).also {
            logger?.logChatMessage(prompt)
            logger?.info { "\n" + prompt.content }
        }
    }

    override fun updateGenerationConfig(
        generationConfig: GenerationConfig,
    ): GenerationStrategy {
        val configUpdate = generationConfig as? LMGenerationConfigUpdate ?: return this

        val mergedRemarks = if (configUpdate.remarks != null) {
            generationConfig.remarks?.plus(this.generationConfig.remarks) ?: emptyList()
        } else {
            this.generationConfig.remarks
        }

        val mergedConfig = LMGenerationConfigContainer(
            modelId = configUpdate.modelId ?: this.generationConfig.modelId,
            temperature = configUpdate.temperature ?: this.generationConfig.temperature,
            maxTokens = configUpdate.maxTokens ?: this.generationConfig.maxTokens,
            lmServerUrl = configUpdate.lmServerUrl ?: this.generationConfig.lmServerUrl,
            lmServerToken = configUpdate.lmServerToken ?: this.generationConfig.lmServerToken,
            contextFilter = configUpdate.contextFilter ?: this.generationConfig.contextFilter,
            promptBuilder = configUpdate.promptBuilder ?: this.generationConfig.promptBuilder,
            remarks = mergedRemarks,
            requestTimeout = configUpdate.requestTimeout ?: this.generationConfig.requestTimeout,
            connectTimeout = configUpdate.connectTimeout ?: this.generationConfig.connectTimeout,
            socketTimeout = configUpdate.socketTimeout ?: this.generationConfig.socketTimeout,
        )

        return LMGenerationStrategy.of(mergedConfig)
    }

    override fun toString(): String {
        return "LMGenerationStrategyImpl(" +
            "planGenerator=$generator, " +
            "generationConfig=$generationConfig)"
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
