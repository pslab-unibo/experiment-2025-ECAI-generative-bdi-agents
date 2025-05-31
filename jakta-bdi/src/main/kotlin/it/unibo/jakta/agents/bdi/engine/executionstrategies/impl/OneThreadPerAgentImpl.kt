package it.unibo.jakta.agents.bdi.engine.executionstrategies.impl

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.AgentLifecycle
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.fsm.Activity
import it.unibo.jakta.agents.fsm.Runner
import it.unibo.jakta.agents.utils.Promise

internal class OneThreadPerAgentImpl : ExecutionStrategy {
    var runnerPromises: Set<Promise<Unit>> = emptySet()

    private lateinit var executionMas: Mas
    private val agentsRunners: MutableMap<Agent, Activity.Controller> = mutableMapOf()

    override fun dispatch(mas: Mas) {
        executionMas = mas
        mas.agents.forEach { agent ->
            val agentLC = AgentLifecycle.newLifecycleFor(agent)
            val promise =
                Runner
                    .threadOf(
                        Activity.of {
                            agentsRunners += agent to it
                            val sideEffects = agentLC.runOneCycle(executionMas.environment, it)
                            executionMas.applyEnvironmentEffects(sideEffects)
                        },
                    ).run()
            runnerPromises += promise
        }
    }

    override fun spawnAgent(agent: Agent) {
        val agentLC = AgentLifecycle.newLifecycleFor(agent)
        val promise =
            Runner
                .threadOf(
                    Activity.of {
                        agentsRunners += agent to it
                        val sideEffects = agentLC.runOneCycle(executionMas.environment, it)
                        executionMas.applyEnvironmentEffects(sideEffects)
                    },
                ).run()
        runnerPromises += promise
    }

    override fun removeAgent(agentName: String) {
        val removedAgentController = agentsRunners.filter { it.key.name == agentName }.values.firstOrNull()
        removedAgentController?.stop()
    }

    override fun shutdown() {
        agentsRunners.clear()
        runnerPromises.forEach { it.cancel(true) }
    }
}
