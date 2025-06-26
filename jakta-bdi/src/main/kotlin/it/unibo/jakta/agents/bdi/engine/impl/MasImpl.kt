package it.unibo.jakta.agents.bdi.engine.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.MasID
import it.unibo.jakta.agents.bdi.engine.actions.effects.AddData
import it.unibo.jakta.agents.bdi.engine.actions.effects.BroadcastMessage
import it.unibo.jakta.agents.bdi.engine.actions.effects.EnvironmentChange
import it.unibo.jakta.agents.bdi.engine.actions.effects.PopMessage
import it.unibo.jakta.agents.bdi.engine.actions.effects.RemoveAgent
import it.unibo.jakta.agents.bdi.engine.actions.effects.RemoveData
import it.unibo.jakta.agents.bdi.engine.actions.effects.SendMessage
import it.unibo.jakta.agents.bdi.engine.actions.effects.SpawnAgent
import it.unibo.jakta.agents.bdi.engine.actions.effects.UpdateData
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.generation.GenerationStrategy
import it.unibo.jakta.agents.bdi.engine.logging.LoggingConfig
import it.unibo.jakta.agents.bdi.engine.logging.loggers.MasLogger
import org.koin.core.module.Module

internal class MasImpl(
    override val masID: MasID,
    override val executionStrategy: ExecutionStrategy,
    override var environment: Environment,
    override var agents: Iterable<Agent>,
    override val generationStrategy: GenerationStrategy? = null,
    override val loggingConfig: LoggingConfig? = null,
    override val logger: MasLogger? = null,
    override val modules: List<Module> = emptyList(),
) : Mas {
    override fun start() = executionStrategy.dispatch(this)

    override fun stop() = executionStrategy.shutdown()

    override fun applyEnvironmentEffects(effects: Iterable<EnvironmentChange>) =
        effects.forEach {
            logger?.log { it }
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
