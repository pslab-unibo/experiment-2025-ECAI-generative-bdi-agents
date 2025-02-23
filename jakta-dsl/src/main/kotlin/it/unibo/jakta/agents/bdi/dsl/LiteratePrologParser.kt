package it.unibo.jakta.agents.bdi.dsl

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.goals.Achieve
import it.unibo.jakta.agents.bdi.goals.Act
import it.unibo.jakta.agents.bdi.goals.ActInternally
import it.unibo.jakta.agents.bdi.goals.AddBelief
import it.unibo.jakta.agents.bdi.goals.Goal
import it.unibo.jakta.agents.bdi.goals.RemoveBelief
import it.unibo.jakta.agents.bdi.goals.Spawn
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.agents.bdi.goals.UpdateBelief
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.parsing.TermParser

object LiteratePrologParser {
    private val toolRegex = "\\[([^]]+)]".toRegex()
    private val expressionRegex = "@(\\w+)".toRegex()
    private val parser = TermParser.withDefaultOperators()

    private fun tangleProlog(input: String): List<String> {
        val terms = toolRegex.findAll(input).map { it.groupValues[1] }.toList()
        val termsWithoutAt = terms.filterNot { it.startsWith("@") }
        return termsWithoutAt.map {
            it.replace(expressionRegex) { match -> match.groupValues[1].capitalize() }
        }
    }

    private fun extractContentInsideParens(input: String): String {
        val regex = """\((.+)\)""".toRegex()
        val res = regex.find(input)?.groupValues?.get(1) ?: input
        return "$res."
    }

    fun tanglePlanBody(input: String): List<Goal> =
        tangleProlog(input).mapNotNull { term ->
            when {
                term.startsWith("achieve") -> Achieve.of(processTerm(term))
                term.startsWith("spawn") -> Spawn.of(processTerm(term))
                term.startsWith("test") -> Test.of(createBelief(term))
                term.startsWith("add") -> AddBelief.of(createBelief(term))
                term.startsWith("remove") -> RemoveBelief.of(Belief.from(processTerm(term)))
                term.startsWith("update") -> UpdateBelief.of(Belief.from(processTerm(term)))
                term.startsWith("execute") -> Act.of(processTerm(term))
                term.startsWith("iact") -> ActInternally.of(processTerm(term))
                else -> null
            }
        }

    private fun processTerm(term: String): Struct =
        parser.parseStruct(extractContentInsideParens(term))

    private fun createBelief(term: String): Belief =
        Belief.wrap(processTerm(term), wrappingTag = Belief.SOURCE_SELF)

    /**
     * Parse a fragment of tangled Prolog code to a [Struct].
     * If the fragment is made of a single clause, return it.
     * If the fragment is made of multiple clauses, put all of them in `and`
     * and return a single [Struct].
     */
    fun tangleStruct(literateProgram: String): Struct? {
        val program = tangleProlog(literateProgram)
        val structs = program.map { parser.parseStruct(it) }
        return if (program.isEmpty()) {
            null
        } else {
            Tuple.wrapIfNeeded(structs).castToStruct()
        }
    }
}
