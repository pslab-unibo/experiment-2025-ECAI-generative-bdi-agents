package it.unibo.jakta.agents.bdi.engine.executionstrategies

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.executionstrategies.impl.DiscreteEventExecutionImpl
import it.unibo.jakta.agents.bdi.engine.executionstrategies.impl.DiscreteTimeExecutionImpl
import it.unibo.jakta.agents.bdi.engine.executionstrategies.impl.OneThreadPerAgentImpl
import it.unibo.jakta.agents.bdi.engine.executionstrategies.impl.OneThreadPerMasImpl

interface ExecutionStrategy {
    fun dispatch(mas: Mas)

    fun shutdown()

    fun spawnAgent(agent: Agent)

    fun removeAgent(agentName: String)

    companion object {
        fun oneThreadPerAgent(): ExecutionStrategy = OneThreadPerAgentImpl()

        fun oneThreadPerMas(): ExecutionStrategy = OneThreadPerMasImpl()

        fun discreteEventExecution(): ExecutionStrategy = DiscreteEventExecutionImpl()

        fun discreteTimeExecution(): ExecutionStrategy = DiscreteTimeExecutionImpl()
    }
}
