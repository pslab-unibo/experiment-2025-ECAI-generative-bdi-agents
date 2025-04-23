package it.unibo.jakta.playground.explorer

import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.goals.TriggerMetadata.meaning
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.playground.explorer.gridworld.GridWorld.Companion.directions
import it.unibo.jakta.playground.explorer.gridworld.GridWorld.Companion.objects

object ExplorerBot {
    fun MasScope.explorerBot(strategy: LMGenerationStrategy? = null) =
        agent("ExplorerBot") {
            generationStrategy = strategy
            goals {
                +achieve("reach"("home")).meaning {
                    "reach a situation where ${args[0]} is in the position of the agent" +
                        " (i.e. there_is(${args[0]}, here))"
                }
            }
            beliefs {
                env.data.directions()?.forEach { fact { "direction"(it) } }
                env.data.objects()?.forEach { fact { "object"(it.key) } }

                fact { "direction"("here") }

                admissible {
                    fact { "obstacle"("Direction") }
                    fact { "there_is"("Object", "Direction") }
                }
            }
        }
}
