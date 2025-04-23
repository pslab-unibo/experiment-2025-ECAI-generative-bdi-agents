package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.Belief.Companion.SOURCE_PERCEPT
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.playground.MockGenerationStrategy.createOneStepStrategyWithMockedAPI
import it.unibo.jakta.playground.explorer.ExplorerBot.explorerBot
import it.unibo.jakta.playground.explorer.gridworld.GridWorld
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var

val getDirectionToMove = object : AbstractExternalAction("getDirectionToMove", "Direction") {
    override val purpose = "provides a Direction free of obstacles where the agent can then move"

    override fun action(request: ExternalRequest) {
        val percepts = request.environment.perception.percept()
        val output = request.arguments[0].castToVar()

        val belief = Belief.wrap(
            Struct.of("not", Struct.of("obstacle", Var.of("Direction"))),
            wrappingTag = SOURCE_PERCEPT,
        )

        val dir = percepts.solveAll(belief)
            .toList()
            .flatMap { it.substitution.values }
            .random()

        this.addResults(Substitution.unifier(output to dir))
    }
}

val move = object : AbstractExternalAction("move", "Direction") {
    override val purpose = "move in the given Direction"

    override fun action(request: ExternalRequest) {
        val direction = request.arguments[0].castToAtom().value
        updateData("directionToMove" to direction)
    }
}

fun main() {
    val strategy = createOneStepStrategyWithMockedAPI(listOf(text1)) // text2

    mas {
        loggingConfig = LoggingConfig()
        generationStrategy = strategy

//        oneStepGeneration {
//            url = "http://localhost:8080/"
//        }

        environment {
            from(GridWorld())
            actions {
                action(move)
                action(getDirectionToMove)
            }
        }
        explorerBot()
    }.start()
}
