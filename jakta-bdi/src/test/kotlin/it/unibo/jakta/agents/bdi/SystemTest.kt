package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy

fun main() {
    val e = Environment.of()

    val alice = Agent.of()
    val bob = Agent.of()

    val agentSystem = Mas.of(ExecutionStrategy.oneThreadPerAgent(), e, alice, bob)

    agentSystem.start()
}
