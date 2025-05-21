package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.engine.Agent
import it.unibo.jakta.agents.bdi.engine.Jakta
import it.unibo.jakta.agents.bdi.engine.Mas
import it.unibo.jakta.agents.bdi.engine.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.engine.actions.impl.AbstractExternalAction
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.jakta.agents.bdi.engine.environment.Environment
import it.unibo.jakta.agents.bdi.engine.events.Event
import it.unibo.jakta.agents.bdi.engine.executionstrategies.ExecutionStrategy
import it.unibo.jakta.agents.bdi.engine.goals.Achieve
import it.unibo.jakta.agents.bdi.engine.goals.Act
import it.unibo.jakta.agents.bdi.engine.goals.ActInternally
import it.unibo.jakta.agents.bdi.engine.messages.Message
import it.unibo.jakta.agents.bdi.engine.messages.Tell
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.engine.plans.PlanLibrary
import it.unibo.jakta.agents.bdi.engine.messages.Achieve as AchieveMsg

fun main() {
    val broadcastAction =
        object : AbstractExternalAction("broadcast", 2) {
            override fun action(request: ExternalRequest) {
                val type = request.arguments[0].castToAtom()
                val message = request.arguments[1].castToStruct()
                when (type.value) {
                    "tell" -> broadcastMessage(Message(request.sender, Tell, message))
                    "achieve" ->
                        broadcastMessage(
                            Message(request.sender, AchieveMsg, message),
                        )
                }
            }
        }

    val env =
        Environment.of(
            externalActions =
                mapOf(
                    broadcastAction.signature.name to broadcastAction,
                ),
        )

    val sender =
        Agent.of(
            name = "sender",
            events =
                listOf(
                    Event.ofAchievementGoalInvocation(Achieve.of(Jakta.parseStruct("broadcast"))),
                ),
            planLibrary =
                PlanLibrary.of(
                    Plan.ofAchievementGoalInvocation(
                        value = Jakta.parseStruct("broadcast"),
                        goals =
                            listOf(
                                ActInternally.of(Jakta.parseStruct("print(\"Broadcast message\")")),
                                Act.of(Jakta.parseStruct("broadcast(tell, greetings)")),
                            ),
                    ),
                ),
        )

    val alice =
        Agent.of(
            name = "alice",
            planLibrary =
                PlanLibrary.of(
                    Plan.ofBeliefBaseAddition(
                        belief = Belief.from(Jakta.parseStruct("greetings(source(Sender))")),
                        goals =
                            listOf(
                                ActInternally.of(Jakta.parseStruct("print(\"Received message from: \", Sender)")),
                            ),
                    ),
                ),
        )

    Mas
        .of(
            ExecutionStrategy.oneThreadPerAgent(),
            env,
            sender,
            alice,
            alice.copy(),
        ).start()
}
