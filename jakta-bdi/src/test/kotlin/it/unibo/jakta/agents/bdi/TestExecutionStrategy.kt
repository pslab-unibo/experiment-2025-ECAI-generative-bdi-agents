package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Jakta
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.actions.InternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractInternalAction
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.goals.Achieve
import it.unibo.jakta.agents.bdi.engine.goals.ActInternally
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

fun main() {
    val alice =
        Agent.of(
            name = "Alice",
            events =
                listOf(
                    Event.ofAchievementGoalInvocation(Achieve.of(Jakta.parseStruct("my_thread"))),
                ),
            planLibrary =
                PlanLibrary.of(
                    Plan.ofAchievementGoalInvocation(
                        value = Jakta.parseStruct("my_thread"),
                        goals =
                            listOf(
                                ActInternally.of(Jakta.parseStruct("thread")),
                            ),
                    ),
                ),
            internalActions =
                mapOf(
                    "thread" to
                        object : AbstractInternalAction("thread", 0) {
                            override fun action(request: InternalRequest) {
                                println("Thread: ${Thread.currentThread().name}")
                            }
                        },
                ),
        )
    val environment = Environment.of()

    Mas.of(ExecutionStrategy.oneThreadPerAgent(), environment, alice, alice.copy()).start()
}
