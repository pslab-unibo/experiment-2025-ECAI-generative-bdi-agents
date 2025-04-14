package it.unibo.jakta.agents.bdi.impl

import io.github.oshai.kotlinlogging.KLogger
import it.unibo.jakta.agents.bdi.Agent
import it.unibo.jakta.agents.bdi.Mas
import it.unibo.jakta.agents.bdi.actions.effects.AddData
import it.unibo.jakta.agents.bdi.actions.effects.BroadcastMessage
import it.unibo.jakta.agents.bdi.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.actions.effects.PopMessage
import it.unibo.jakta.agents.bdi.actions.effects.RemoveAgent
import it.unibo.jakta.agents.bdi.actions.effects.RemoveData
import it.unibo.jakta.agents.bdi.actions.effects.SendMessage
import it.unibo.jakta.agents.bdi.actions.effects.SpawnAgent
import it.unibo.jakta.agents.bdi.actions.effects.UpdateData
import it.unibo.jakta.agents.bdi.environment.Environment
import it.unibo.jakta.agents.bdi.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.logging.implementation
import it.unibo.jakta.agents.bdi.plangeneration.GenerationStrategy

internal class MasImpl(
    override val executionStrategy: ExecutionStrategy,
    override var environment: Environment,
    override var agents: Iterable<Agent>,
    override val generationStrategy: GenerationStrategy? = null,
    override val loggingConfig: LoggingConfig? = null,
    override val logger: KLogger? = null,
) : Mas {

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
}
