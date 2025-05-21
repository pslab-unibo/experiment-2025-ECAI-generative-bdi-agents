package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Jakta
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.goals.Achieve
import it.unibo.jakta.agents.bdi.engine.goals.ActInternally
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary

fun main() {
    val start = Jakta.parseStruct("start")
    val sleepingAgent =
        Agent.of(
            name = "Sleeping Agent",
            beliefBase = BeliefBase.of(Belief.fromSelfSource(Jakta.parseStruct("run"))),
            events = listOf(Event.ofAchievementGoalInvocation(Achieve.of(start))),
            planLibrary =
                PlanLibrary.of(
                    Plan.ofAchievementGoalInvocation(
                        value = Jakta.parseStruct("start"),
                        goals =
                            listOf(
                                ActInternally.of(Jakta.parseStruct("print(\"Before Sleep\")")),
                                ActInternally.of(Jakta.parseStruct("sleep(5000)")),
                                ActInternally.of(Jakta.parseStruct("print(\"After Sleep\")")),
                                ActInternally.of(Jakta.parseStruct("stop")),
                            ),
                    ),
                ),
        )
    Mas.of(ExecutionStrategy.oneThreadPerMas(), Environment.of(), sleepingAgent).start()
}
