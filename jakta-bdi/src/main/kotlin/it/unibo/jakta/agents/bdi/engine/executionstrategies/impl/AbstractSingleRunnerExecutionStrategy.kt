package it.unibo.jakta.agents.bdi.engine.executionstrategies.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.AgentLifecycle
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy

internal abstract class AbstractSingleRunnerExecutionStrategy : ExecutionStrategy {
    protected val synchronizedAgents = SynchronizedAgents()

    override fun spawnAgent(agent: Agent) {
        synchronizedAgents.addAgent(agent)
    }

    override fun removeAgent(agentName: String) {
        synchronizedAgents.removeAgent(agentName)
    }

    internal class SynchronizedAgents {
        private var agents: Map<Agent, AgentLifecycle> = emptyMap()

        @Synchronized
        fun addAgent(agent: Agent) {
            agents = agents + (agent to AgentLifecycle.newLifecycleFor(agent))
        }

        @Synchronized
        fun removeAgent(agentName: String) {
            agents = agents.filter { it.key.name != agentName }
        }

        @Synchronized
        fun getAgents(): Map<Agent, AgentLifecycle> = agents
    }
}
