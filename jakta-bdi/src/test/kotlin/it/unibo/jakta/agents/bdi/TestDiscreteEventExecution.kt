package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Jakta
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.actions.InternalActions
import it.unibo.jakta.agents.bdi.engine.actions.InternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractInternalAction
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.executionstrategies.setTimeDistribution
import it.unibo.jakta.agents.bdi.engine.goals.Achieve
import it.unibo.jakta.agents.bdi.engine.goals.ActInternally
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary
import it.unibo.jakta.agents.fsm.time.SimulatedTime
import it.unibo.jakta.agents.fsm.time.Time

fun main() {
    val dummyAction =
        object : AbstractInternalAction("time", 0) {
            override fun action(request: InternalRequest) {
                println("time: ${request.requestTimestamp}")
            }
        }
    val alice =
        Agent
            .of(
                events = listOf(Event.ofAchievementGoalInvocation(Achieve.of(Jakta.parseStruct("time")))),
                internalActions = InternalActions.default() + (dummyAction.signature.name to dummyAction),
                planLibrary =
                    PlanLibrary.of(
                        Plan.ofAchievementGoalInvocation(
                            value = Jakta.parseStruct("time"),
                            goals =
                                listOf(
                                    ActInternally.of(Jakta.parseStruct("time")),
                                    Achieve.of(Jakta.parseStruct("time")),
                                ),
                        ),
                    ),
            ).setTimeDistribution { Time.continuous((it as SimulatedTime).value + 5.0) }

    Mas.of(ExecutionStrategy.discreteEventExecution(), Environment.of(), alice).start()
}
