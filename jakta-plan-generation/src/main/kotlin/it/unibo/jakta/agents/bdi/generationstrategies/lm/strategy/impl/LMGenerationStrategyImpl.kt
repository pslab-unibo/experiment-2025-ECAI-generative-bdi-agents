package it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.impl

import it.unibo.jakta.agents.bdi.engine.AgentID
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationConfig
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationResult
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationState
import it.unibo.jakta.agents.bdi.engine.plangeneration.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.plangeneration.PgpID
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationConfig
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.LMGenerationState
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.events.LMGenerationCompleted
import it.unibo.jakta.agents.bdi.generationstrategies.lm.logging.loggers.LMPGPLogger
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.generation.LMPlanGenerator
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable

@Serializable
internal class LMGenerationStrategyImpl(
    override val generator: LMPlanGenerator,
    override val generationConfig: LMGenerationConfig.LMGenerationConfigContainer,
) : LMGenerationStrategy {
    override fun requestBlockingGeneration(generationState: GenerationState): GenerationResult =
        runBlocking {
            val lmState = generationState as? LMGenerationState
            return@runBlocking if (lmState != null) {
                generator.generate(this@LMGenerationStrategyImpl, lmState)
            } else {
                LMGenerationFailure(generationState, LMGenerationStrategy.configErrorMsg(generationState))
            }
        }

    override fun initializeGeneration(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
        masID: MasID?,
        agentID: AgentID?,
        loggingConfig: LoggingConfig?,
    ): GenerationState {
        val promptBuilder = generationConfig.promptBuilder
        val promptMsg =
            promptBuilder.build(
                initialGoal,
                context,
                externalActions,
                generationConfig.contextFilter,
                generationConfig.remarks,
            )
        val logger =
            loggingConfig?.let { cfg ->
                if (masID != null && agentID != null) {
                    val pgpID = PgpID()
                    LMPGPLogger.create(masID, agentID, pgpID, cfg)
                } else {
                    null
                }
            }

        return LMGenerationState(
            goal = initialGoal,
            logger = logger,
            chatHistory = listOf(promptMsg),
        ).also {
            logger?.log { LMGenerationCompleted(promptMsg) }
        }
    }

    override fun updateGenerationConfig(generationConfig: GenerationConfig): GenerationStrategy {
        val configUpdate = generationConfig as? LMGenerationConfig.LMGenerationConfigUpdate ?: return this

        val mergedRemarks =
            if (configUpdate.remarks != null) {
                generationConfig.remarks?.plus(this.generationConfig.remarks) ?: emptyList()
            } else {
                this.generationConfig.remarks
            }

        val mergedConfig =
            LMGenerationConfig.LMGenerationConfigContainer(
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

    override fun toString(): String =
        "LMGenerationStrategyImpl(" +
            "planGenerator=$generator, " +
            "generationConfig=$generationConfig)"
}
