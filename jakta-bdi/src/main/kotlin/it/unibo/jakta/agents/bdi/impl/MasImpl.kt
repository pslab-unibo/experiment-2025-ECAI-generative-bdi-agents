package it.unibo.jakta.agents.bdi.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.Mas
import it.unibo.jakta.agents.bdi.actions.effects.AddData
import it.unibo.jakta.agents.bdi.actions.effects.BeliefChange
import it.unibo.jakta.agents.bdi.actions.effects.BroadcastMessage
import it.unibo.jakta.agents.bdi.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.actions.effects.EventChange
import it.unibo.jakta.agents.bdi.actions.effects.PlanChange
import it.unibo.jakta.agents.bdi.actions.effects.PopMessage
import it.unibo.jakta.agents.bdi.actions.effects.RemoveAgent
import it.unibo.jakta.agents.bdi.actions.effects.RemoveData
import it.unibo.jakta.agents.bdi.actions.effects.SendMessage
import it.unibo.jakta.agents.bdi.actions.effects.SpawnAgent
import it.unibo.jakta.agents.bdi.actions.effects.UpdateData
import it.unibo.jakta.agents.bdi.context.ContextUpdate.ADDITION
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.logging.LoggerFactory.createLogger
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.logging.events.ActionAddition
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy
import java.util.UUID

internal class MasImpl(
    override val executionStrategy: ExecutionStrategy,
    override var environment: Environment,
    override var agents: Iterable<Agent>,
    override val generationStrategy: GenerationStrategy? = null,
    override val loggingConfig: LoggingConfig? = null,
    override val logger: KLogger? = null,
) : Mas {
    init {
        initializeAgents()
        addAgentsToEnvironment()
        logInitialState()
    }

    private fun initializeAgents() {
        agents = agents.map { agent ->
            val agentloggingConfig = loggingConfig?.copy(
                logDir = "${loggingConfig.logDir}/${agent.name}",
            )
            agent
                .assignLogger(agentloggingConfig)
                .assignGenerationStrategy(generationStrategy)
                .assignGenerationStrategyToPlans(agentloggingConfig)
        }
    }

    private fun addAgentsToEnvironment() {
        agents.forEach { environment = environment.addAgent(it) }
    }

    private fun logInitialState() {
        logExternalActions()
        agents.forEach { logAgentState(it) }
    }

    private fun logExternalActions() {
        environment.externalActions.values.forEach { action ->
            logger?.implementation(ActionAddition(action))
        }
    }

    private fun logAgentState(agent: Agent) {
        agent.context.events.forEach { event ->
            agent.logger?.implementation(EventChange(event, ADDITION))
        }
        agent.context.planLibrary.plans.forEach { plan ->
            agent.logger?.implementation(PlanChange(plan, ADDITION))
        }
        agent.context.beliefBase.forEach { belief ->
            agent.logger?.implementation(BeliefChange(belief, ADDITION))
        }
        agent.context.internalActions.values.forEach { action ->
            agent.logger?.implementation(ActionAddition(action))
        }
    }

    override fun start() = executionStrategy.dispatch(this)

    override fun applyEnvironmentEffects(effects: Iterable<EnvironmentChange>) = effects.forEach {
        logger?.implementation(it)
        when (it) {
            is BroadcastMessage -> environment = environment.broadcastMessage(it.message)
            is RemoveAgent -> handleRemoveAgent(it)
            is SendMessage -> environment = environment.submitMessage(it.recipient, it.message)
            is SpawnAgent -> handleSpawnAgent(it)
            is AddData -> environment = environment.addData(it.key, it.value)
            is RemoveData -> environment = environment.removeData(it.key)
            is UpdateData -> environment = environment.updateData(it.newData)
            is PopMessage -> environment = environment.popMessage(it.agentName)
        }
    }

    private fun handleRemoveAgent(effect: RemoveAgent) {
        agents = agents.filter { it.name != effect.agentName }
        executionStrategy.removeAgent(effect.agentName)
        environment = environment.removeAgent(effect.agentName)
    }

    private fun handleSpawnAgent(effect: SpawnAgent) {
        agents += effect.agent
        executionStrategy.spawnAgent(effect.agent)
        environment = environment.addAgent(effect.agent)
    }

    companion object {
        private fun Agent.assignLogger(loggingConfig: LoggingConfig?): Agent {
            val logger = loggingConfig?.let { createLogger(it, this.name) }
            return this.copy(logger = logger)
        }

        private fun Agent.assignGenerationStrategy(masGenerationStrategy: GenerationStrategy?): Agent {
            return if (this.generationStrategy == null && masGenerationStrategy != null) {
                this.copy(generationStrategy = masGenerationStrategy.copy())
            } else {
                this
            }
        }

        private fun Agent.assignGenerationStrategyToPlans(loggingConfig: LoggingConfig?): Agent {
            val agentGenerationStrategy = this.generationStrategy
            val updatedPlans = this.context.planLibrary.plans.map { plan ->
                plan.assignGenerationStrategyAndLogger(agentGenerationStrategy, loggingConfig)
            }
            return this.copy(planLibrary = PlanLibrary.of(updatedPlans))
        }

        private fun Plan.assignGenerationStrategyAndLogger(
            agentGenerationStrategy: GenerationStrategy?,
            loggingConfig: LoggingConfig?,
        ): Plan {
            return if (this is GeneratedPlan) {
                val strategyToApply = agentGenerationStrategy ?: return this
                val loggerName = UUID.randomUUID().toString()
                val agentLogPath = loggingConfig?.logDir
                val conversationLoggingConfig = loggingConfig?.copy(
                    logDir = "$agentLogPath/chat",
                )
                val logger = conversationLoggingConfig?.let { createLogger(it, loggerName) }
                GeneratedPlan.of(
                    this.id,
                    this.trigger,
                    this.guard,
                    this.goals,
                    generationStrategy = strategyToApply.copy(logger = logger),
                    this.literateTrigger,
                    this.literateGuard,
                    this.literateGoals,
                )
            } else {
                this
            }
        }
    }
}
