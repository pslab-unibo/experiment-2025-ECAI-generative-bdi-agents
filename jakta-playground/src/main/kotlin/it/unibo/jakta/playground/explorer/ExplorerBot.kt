package it.unibo.jakta.playground.explorer

import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.Belief.Companion.SOURCE_PERCEPT
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.beliefs.BeliefMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.goals.TriggerMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.plans
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.playground.explorer.gridworld.GridWorld
import it.unibo.jakta.playground.explorer.gridworld.GridWorld.Companion.directions
import it.unibo.jakta.playground.explorer.gridworld.GridWorld.Companion.objects
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var

object ExplorerBot {
    fun baselinePlans() =
        plans {
            +achieve("reach"(O)) onlyIf {
                "there_is"(O, "here").fromPercept
            } then {
                execute("print"("reached", O))
                execute("stop")
            }
            +achieve("reach"(O)) onlyIf {
                not("there_is"(O, "here").fromPercept)
            } then {
                execute("getDirectionToMove"(D))
                achieve("move_towards"(D, O))
            }
            +achieve("move_towards"(D, O)) onlyIf {
                "direction"(D).fromSelf and
                    not("obstacle"(D)).fromPercept
            } then {
                execute("move"(D))
                achieve("reach"(O))
            }
            +achieve("move_towards"(D, O)) onlyIf {
                "direction"(D).fromSelf and
                    "obstacle"(D).fromPercept
            } then {
                execute("getDirectionToMove"(N))
                achieve("move_towards"(N, O))
            }
        }

    fun MasScope.explorerBot(plans: Iterable<Plan>? = null, strategy: LMGenerationStrategy? = null) =
        agent("ExplorerBot") {
            generationStrategy = strategy
            goals {
                admissible {
                    +achieve("reach"("O")).meaning {
                        "reach a situation where ${args[0]} is in the position of the agent" +
                            " (i.e. there_is(${args[0]}, here))"
                    }
                }
                +achieve("reach"("home"))
            }
            beliefs {
                env.data.directions()?.forEach {
                    +fact { "direction"(it) }.meaning {
                        "${args[0]} is a $functor"
                    }
                }
                env.data.objects()?.forEach {
                    +fact { "object"(it.key) }.meaning {
                        "${args[0]} is an $functor"
                    }
                }

                +fact { "direction"("here") }.meaning {
                    "${args[0]} denotes the null $functor w.r.t. the agent's current location"
                }

                admissible {
                    +fact { "obstacle"("D") }.meaning {
                        "there is an $functor to the ${args[0]}"
                    }
                    +fact { "there_is"("O", "D") }.meaning {
                        "there is an ${args[0]} in the given ${args[1]}"
                    }
                    +fact { "direction"("D") }.meaning {
                        "${args[0]} is a direction"
                    }
                    +fact { "object"("O") }.meaning {
                        "${args[0]} is an object"
                    }
                }
            }
            plans?.let { plans(it) }
        }

    val getDirectionToMove = object : AbstractExternalAction("getDirectionToMove", "Direction") {
        override fun action(request: ExternalRequest) {
            val percepts = request.environment.perception.percept()
            val output = request.arguments[0].asVar()

            val belief = Belief.wrap(
                Struct.of("not", Struct.of("obstacle", Var.of("Direction"))),
                wrappingTag = SOURCE_PERCEPT,
            )

            val dir = percepts.solveAll(belief, ignoreSource = true)
                .toList()
                .flatMap { it.substitution.values }
                .random()

            if (output != null) {
                this.addResults(Substitution.unifier(output to dir))
            }
        }
    }

    val move = object : AbstractExternalAction("move", "Direction") {
        override fun action(request: ExternalRequest) {
            val direction = request.arguments[0].asAtom()?.value
            if (direction != null) {
                updateData("directionToMove" to direction)
            }
        }
    }

    fun MasScope.gridWorld() = environment {
        from(GridWorld())
        actions {
            action(move).meaning {
                "move in the given ${args[0]}"
            }
            action(getDirectionToMove).meaning {
                "provides a ${args[0]} free of obstacles where the agent can then move"
            }
        }
    }
}
