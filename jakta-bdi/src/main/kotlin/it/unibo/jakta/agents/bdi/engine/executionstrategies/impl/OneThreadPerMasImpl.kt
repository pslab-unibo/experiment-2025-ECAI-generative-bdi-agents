package it.unibo.jakta.agents.bdi.engine.executionstrategies.impl

import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.fsm.Activity
import it.unibo.jakta.agents.fsm.Runner
import it.unibo.jakta.agents.utils.Promise

internal class OneThreadPerMasImpl : AbstractSingleRunnerExecutionStrategy() {
    override var runnerPromise: Promise<Unit>? = null

    override fun dispatch(mas: Mas) {
        mas.agents.forEach { synchronizedAgents.addAgent(it) }
        val promise =
            Runner
                .threadOf(
                    Activity.of {
                        synchronizedAgents.getAgents().forEach { (_, agentLC) ->
                            val sideEffects = agentLC.runOneCycle(mas.environment, it)
                            mas.applyEnvironmentEffects(sideEffects)
                        }
                        synchronizedAgents.getAgents().ifEmpty { it.stop() }
                    },
                ).run()
        this.runnerPromise = promise
    }

    override fun shutdown() {
        synchronizedAgents.clear()
        runnerPromise?.cancel(true)
    }
}
