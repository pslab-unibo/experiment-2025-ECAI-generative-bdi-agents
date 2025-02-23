package it.unibo.jakta.agents.bdi.impl

import io.github.oshai.kotlinlogging.KotlinLogging.logger
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
import it.unibo.jakta.agents.bdi.logging.LoggerFactory
import it.unibo.jakta.agents.bdi.logging.LoggingStrategy
import it.unibo.jakta.agents.bdi.logging.events.ActionAddition
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.plans.generation.GenerationStrategy

internal class MasImpl(
    override val executionStrategy: ExecutionStrategy,
    override var environment: Environment,
    override var agents: Iterable<Agent>,
    override val generationStrategy: GenerationStrategy? = null,
    override val loggingStrategy: LoggingStrategy? = null,
) : Mas {
    init {
        agents = agents
            .map { assignLoggerToAgent(it, loggingStrategy) }
            .map { assignGenStrategyToAgent(it, generationStrategy) }
            .map { assignGenStrategyToPlan(it) }

        agents.forEach { environment = environment.addAgent(it) }

        environment.externalActions.values.forEach { act -> logger.implementation(ActionAddition(act)) }
        agents.forEach { agt ->
            agt.context.events.forEach { agt.logger?.implementation(EventChange(it, ADDITION)) }
            agt.context.planLibrary.plans.forEach { agt.logger?.implementation(PlanChange(it, ADDITION)) }
            agt.context.beliefBase.forEach { agt.logger?.implementation(BeliefChange(it, ADDITION)) }
            agt.context.internalActions.values.forEach { agt.logger?.implementation(ActionAddition(it)) }
        }
    }

    override fun start() = executionStrategy.dispatch(this)

    override fun applyEnvironmentEffects(effects: Iterable<EnvironmentChange>) = effects.forEach {
        logger.implementation(it)
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
        private fun assignGenStrategyToAgent(agent: Agent, generationStrategy: GenerationStrategy?) =
            if (agent.generationStrategy == null) {
                agent.copy(generationStrategy = generationStrategy)
            } else {
                agent
            }

        private fun assignGenStrategyToPlan(agent: Agent): Agent =
            agent.generationStrategy?.let { genStrategy ->
                val updatedPlans = agent.context.planLibrary.plans.map { plan ->
                    if (plan is GeneratedPlan && plan.generationStrategy == null) {
                        plan.withGenerationStrategy(generationStrategy = genStrategy)
                    } else {
                        plan
                    }
                }
                agent.copy(planLibrary = PlanLibrary.of(updatedPlans))
            } ?: agent

        private fun assignLoggerToAgent(agent: Agent, loggingStrategy: LoggingStrategy?): Agent {
            val logger = if (loggingStrategy != null) {
                LoggerFactory.createLogger(loggingStrategy, agent.name)
            } else {
                logger(agent.name)
            }
            return agent.copy(logger = logger)
        }
    }
}
