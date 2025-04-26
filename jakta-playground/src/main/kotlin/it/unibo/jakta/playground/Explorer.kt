package it.unibo.jakta.playground

import it.unibo.jakta.agents.bdi.actions.ExternalRequest
import it.unibo.jakta.agents.bdi.actions.impl.AbstractExternalAction
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.Belief.Companion.SOURCE_PERCEPT
import it.unibo.jakta.agents.bdi.dsl.mas
import it.unibo.jakta.agents.bdi.logging.LoggingConfig
import it.unibo.jakta.playground.MockGenerationStrategy.createOneStepStrategyWithMockedAPI
import it.unibo.jakta.playground.explorer.ExplorerBot.explorerBot
import it.unibo.jakta.playground.explorer.ExplorerBot.gridWorld
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var

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

fun main() {
    val strategy = createOneStepStrategyWithMockedAPI(listOf(text6))

    mas {
        loggingConfig = LoggingConfig()
        generationStrategy = strategy

        gridWorld()
        explorerBot()
    }.start()
}
