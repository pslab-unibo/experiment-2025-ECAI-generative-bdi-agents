package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.llm.LLMConfiguration
import it.unibo.jakta.llm.PlanPaths
import it.unibo.jakta.llm.tools.PrintTool
import it.unibo.jakta.llm.tools.StopTool

fun MasScope.printerAgent() =
    agent("printer") {
        goals {
            achieve("OrderedGoalExecution")
        }
    }

fun main() {
    val cfg =
        LLMConfiguration(
            taskName = "OrderedPrints",
            goalName = "OrderedGoalExecution",
            goalDescription = "Create a plan that prints 2 3 1 3 in this order and stops the agent at the end.",
            planPaths = PlanPaths(),
            tools = listOf(PrintTool(), StopTool()),
            observations = "The agent must stop only once, when all the numbers in the sequence are printed.",
        )

    mas {
        executionStrategy = LLMExecutionStrategy(cfg)
        printerAgent()
    }.start()
}
