package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.Mas
import it.unibo.jakta.agents.bdi.executionstrategies.impl.AbstractSingleRunnerExecutionStrategy
import it.unibo.jakta.agents.fsm.Activity
import it.unibo.jakta.agents.fsm.Runner
import it.unibo.jakta.llm.LLMConfiguration

// private val askLLM =
//    internalAction("askLLM", 0) {
//        val prompt =
//            Prompt(
//                actions = cfg.tools.joinToString("\n\n") { it.metadata.toString() },
//                observations = cfg.observations,
//                goalName = cfg.goalName,
//                goalDescription = cfg.goalDescription,
//            ).buildPrompt()
//
//        runBlocking {
//            launch {
//                val caller = LLMCaller()
//                val message = caller.callLLM(prompt)
//                if (message != null) {
//                    val loader = PlanLoader(cfg)
//                    val plan = loader.loadPlan(message.action)
//                    if (plan != null) {
//                        addPlan(plan)
//                    } else {
//                        stopAgent()
//                    }
//                } else {
//                    stopAgent()
//                }
//            }
//        }
//    }

class LLMExecutionStrategy(
    val cfg: LLMConfiguration,
) : AbstractSingleRunnerExecutionStrategy() {
    override fun dispatch(
        mas: Mas,
        debugEnabled: Boolean,
    ) {
        mas.agents.forEach { synchronizedAgents.addAgent(it) }
        Runner.Companion
            .threadOf(
                Activity.Companion.of {
                    synchronizedAgents.getAgents().forEach { (_, agentLC) ->
                        val sideEffects = agentLC.runOneCycle(mas.environment, it, debugEnabled, cfg)
                        mas.applyEnvironmentEffects(sideEffects)
                    }
                    synchronizedAgents.getAgents().ifEmpty { it.stop() }
                },
            ).run()
    }
}
