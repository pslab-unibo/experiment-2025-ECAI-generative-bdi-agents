package it.unibo.jakta.playground.explorer.agents

import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.beliefs.BeliefMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.goals.TriggerMetadata.meaning
import it.unibo.jakta.agents.bdi.dsl.plans
import it.unibo.jakta.agents.bdi.engine.plans.Plan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.tuprolog.core.Var

object ExplorerRobot {
    private val Direction = Var.of("Direction")
    private val Object = Var.of("Object")

    val baselinePlans =
        plans {
            +achieve("reach"(Object)) onlyIf {
                "there_is"(Object, "here").fromPercept
            }
            +achieve("reach"(Object)) onlyIf {
                "there_is"(Object, Direction).fromPercept
            } then {
                execute("move"(Direction))
            }
            +achieve("reach"(Object)) onlyIf {
                not("there_is"(Object, `_`).fromPercept)
            } then {
                execute("getDirectionToMove"(Direction))
                execute("move"(Direction))
                achieve("reach"(Object))
            }
        }

    fun MasScope.explorerRobot(
        plans: Iterable<Plan>? = null,
        strategy: LMGenerationStrategy? = null,
    ) = agent("ExplorerRobot") {
        generationStrategy = strategy
        goals {
            admissible {
                +achieve("reach"("Object")).meaning {
                    "reach a situation where ${args[0]} is in the position of the agent" +
                        " (i.e. there_is(${args[0]}, here))"
                }
            }
            +achieve("reach"("home"))
        }
        beliefs {
            admissible {
                +fact { "obstacle"("Direction") }.meaning {
                    "there is an $functor to the ${args[0]}"
                }
                +fact { "there_is"("Object", "Direction") }.meaning {
                    "there is an ${args[0]} in the given ${args[1]}"
                }
                +fact { "direction"("Direction") }.meaning {
                    "${args[0]} is a direction"
                }
                +fact { "object"("Object") }.meaning {
                    "${args[0]} is an object"
                }
            }
        }
        plans?.let { plans(it) }
    }
}
