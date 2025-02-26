package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.Jakta.parseStruct
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
import it.unibo.tuprolog.core.parsing.ParseException

object LiteratePrologParser {
    private val prologFragmentRegex = "\\[([^]]+)]".toRegex()
    private val expressionRegex = "@(\\w+)".toRegex()

    private fun tangleProlog(input: String): List<String> {
        val terms = prologFragmentRegex.findAll(input).map { it.groupValues[1] }.toList()
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

    private fun processTerm(term: String): ParseResult =
        parse(extractContentInsideParens(term))

    private fun parse(input: String): ParseResult =
        try {
            val struct = parseStruct(input)
            ParseResult(struct)
        } catch (e: ParseException) {
            ParseResult(errorMsg = e.message)
        }

    fun tanglePlanBody(input: String): List<Goal> =
        tangleProlog(input).mapNotNull { term ->
            when {
                term.startsWith("achieve") -> processTerm(term).struct?.let { Achieve.Companion.of(it) }
                term.startsWith("spawn") -> processTerm(term).struct?.let { Spawn.Companion.of(it) }
                term.startsWith("test") -> processTerm(term).struct?.let {
                    Belief.Companion.from(it)
                }?.let { Test.Companion.of(it) }
                term.startsWith("add") -> processTerm(term).struct?.let {
                    Belief.Companion.from(it)
                }?.let { AddBelief.Companion.of(it) }
                term.startsWith("remove") -> processTerm(term).struct?.let {
                    Belief.Companion.from(it)
                }?.let { RemoveBelief.Companion.of(it) }
                term.startsWith("update") -> processTerm(term).struct?.let {
                    Belief.Companion.from(it)
                }?.let { UpdateBelief.Companion.of(it) }
                term.startsWith("execute") -> processTerm(term).struct?.let { Act.Companion.of(it) }
                term.startsWith("iact") -> processTerm(term).struct?.let { ActInternally.Companion.of(it) }
                else -> null
            }
        }

    /**
     * Parse a fragment of tangled Prolog code to a [Struct].
     * If the fragment is made of a single [Struct], return it.
     * If the fragment is made of multiple [Struct]s, put all of them in `and`
     * and return a single [Struct].
     */
    fun tangleStruct(literateProgram: String): Struct? {
        val program = tangleProlog(literateProgram)
        return if (program.isEmpty()) {
            null
        } else {
            val structs = program.mapNotNull { parse(it).struct }
            if (structs.isEmpty()) {
                null
            } else if (structs.size == 1) {
                structs[0]
            } else {
                Tuple.Companion.wrapIfNeeded(structs).castToStruct()
            }
        }
    }
}

data class ParseResult(
    val struct: Struct? = null,
    val errorMsg: String? = null,
)
