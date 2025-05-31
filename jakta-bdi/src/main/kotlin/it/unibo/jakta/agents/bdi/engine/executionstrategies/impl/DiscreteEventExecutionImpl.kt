package it.unibo.jakta.agents.bdi.engine.executionstrategies.impl

import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.executionstrategies.hasTimeDistribution
import it.unibo.jakta.agents.bdi.engine.executionstrategies.timeDistribution
import it.unibo.jakta.agents.fsm.Activity
import it.unibo.jakta.agents.fsm.Runner
import it.unibo.jakta.agents.fsm.time.Time
import it.unibo.jakta.agents.utils.Promise

internal class DiscreteEventExecutionImpl : AbstractSingleRunnerExecutionStrategy() {
    override var runnerPromise: Promise<Unit>? = null

    override fun dispatch(mas: Mas) {
        mas.agents.forEach {
            if (!it.hasTimeDistribution) {
                error("ERROR: Can't run a DiscreteEventExecution for agents without a time distribution")
            }
        }
        var time = Time.continuous(0.0)
        mas.agents.forEach { synchronizedAgents.addAgent(it) }
        val promise =
            Runner
                .simulatedOf(
                    Activity.of { act ->
                        // Compute next executions
                        val timeDistributions = mas.agents.associateWith { it.timeDistribution.invoke(time) }
                        val nextEventTime = timeDistributions.values.minOf { it }
                        val agentsToExecute =
                            timeDistributions
                                .filter {
                                    it.value == nextEventTime
                                }.keys
                                .map { it.agentID }

                        // Update time
                        time = nextEventTime

                        // Run Agents
                        synchronizedAgents
                            .getAgents()
                            .filter { (agent, _) -> agentsToExecute.contains(agent.agentID) }
                            .forEach { (_, agentLC) ->
                                val sideEffects = agentLC.runOneCycle(mas.environment, act)
                                mas.applyEnvironmentEffects(sideEffects)
                            }
                        synchronizedAgents.getAgents().ifEmpty { act.stop() }
                    },
                    currentTime = { time },
                ).run()
        this.runnerPromise = promise
    }

    override fun shutdown() {
        synchronizedAgents.clear()
        runnerPromise?.cancel(true)
    }
}
