package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.dsl.AgentScope
import it.unibo.jakta.agents.bdi.dsl.MasScope
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.generationstrategies.lm.strategy.LMGenerationStrategy
import it.unibo.jakta.playground.BlocksWorldLiterate.gripperOperator

fun main() = mas {
    gripperOperator()
    loggingConfig = LoggingConfig(logToConsole = true, logToFile = true)
    generationStrategy = LMGenerationStrategy.react {}
}.start()

const val GOAL = """
    The goal is to use the gripper to arrange the blocks such that:

    - block red is on top of block orange,
    - block green is on top of block red.
    """

object BlocksWorldLiterate {
    fun MasScope.gripperOperator() =
        agent("gripperOperator") {
            goals { achieve(GOAL) }
            beliefs {
                fact { "ontable"("red") }
                fact { "on"("green", "red") }
                fact { "on"("blue", "green") }
                fact { "clear"("blue") }
                fact { "ontable"("orange") }
                fact { "on"("purple", "orange") }
                fact { "on"("yellow", "purple") }
                fact { "clear"("yellow") }
                fact { "gripperEmpty" }

                fact { "block"("red") }
                fact { "block"("green") }
                fact { "block"("blue") }
                fact { "block"("orange") }
                fact { "block"("purple") }
                fact { "block"("yellow") }
            }
            plans { plans() }
        }

    fun AgentScope.plans() = plans {
        +achieve("[pickup(@block)]") onlyIf (
            """
            To pick up [@block], three conditions must hold:

            - make sure that [ontable(@block)] is true;
            - nothing can be on top of it (check [clear(@block)]);
            - the gripper has to be empty, so [gripperEmpty] must hold.

            If all that checks out, the gripper can grab the [@block].
            """
            ) then (
            """
            Once the gripper grabs the [@block]:

            - the gripper is now holding it, so [holding(@block)] becomes true;
            - the gripper isn't empty anymore [not(handempty))];
            - since the gripper is holding it, [not(clear(@block))] holds;
            - the block is no longer on the table so, [not(ontable(@block))].
            """
            )

        +achieve("[unstack(@x, @y)] to unstack block [@x] from block [@y]") onlyIf (
            """
            If [on(@x, @y)], the gripper can pick [@x] up as long as:

            - [gripperEmpty] holds;
            - there's nothing on top of the block the gripper will grab ([clear(@x)]).
            """
            ) then (
            """
            Once the gripper lifts block [@x]:

            - the gripper is now [holding(@x)];
            - the gripper is no longer empty [!gripperEmpty];
            - [@x] is no longer clear because it’s hold by the gripper [!clear(@x)].
            - [clear(@y)] since nothing is on top of it anymore.
            - [@x] is no longer stacked on [@y], so [!on(@x, @y)].
            """
            )

        +achieve("[putdown(@block)]") onlyIf (
            "If the gripper is [@holding(@block)], it can put [@block] down on the table."
            ) then (
            """
            When the gripper puts [@block] down on the table:

            - [ontable(@block)];
            - the gripper is no longer [!holding(@block)];
            - [gripperEmpty];
            - [clear(@block)], meaning another block can be placed on top of it.
            """
            )

        +achieve("[stack(@x, @y)] to stack block [@x] on top of block [@y]") onlyIf (
            """
            If the gripper is [holding(@x)], it can stack block [@x] onto block [@y]
             as long as [clear(@x)] holds.
            """
            ) then (
            """
            When the gripper stacks [@x] on top of [@y]:

            - [on(@x, @y)] becomes true.
            - [!holding(@x)].
            - [gripperEmpty] again.
            - [!clear(@y)], since something is now on top of it.
            - [clear(@x)], since nothing is on top of it yet.
            """
            )

        +achieve(GOAL) given { generate = true }
    }
}
