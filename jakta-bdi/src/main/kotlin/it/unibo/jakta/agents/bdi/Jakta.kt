package it.unibo.jakta.agents.bdi

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.TermParser
import java.util.Locale

object Jakta {
    // fun convert1(struct: Struct): Term = struct.accept(jasonTo2p)

    val operators = OperatorSet(
        Operator("&", Specifier.XFY, 1000),
        Operator("|", Specifier.XFY, 1100),
        Operator("~", Specifier.FX, 900),
    )

    val termFormatter: TermFormatter = TermFormatter.prettyExpressions(
        operatorSet = OperatorSet.DEFAULT + operators,
    )

    private val parser = TermParser.withOperators(OperatorSet.DEFAULT + operators)

    fun parseStruct(string: String): Struct = parser.parseStruct(string)

    fun parseClause(string: String): Clause = parser.parseClause(string)

    fun printAslSyntax(agent: Agent, prettyFormatted: Boolean = true) {
        println("% ${agent.name}")
        for (belief in agent.context.beliefBase) {
            if (prettyFormatted) {
                println(termFormatter.format(belief.rule))
            } else {
                println(belief.rule)
            }
        }
        for (plan in agent.context.planLibrary.plans) {
            var trigger = plan.trigger.value.toString()
            var guard = plan.guard.toString()
            var body = plan.goals.joinToString("; ") { it.value.toString() }
            if (prettyFormatted) {
                trigger = termFormatter.format(plan.trigger.value)
                guard = termFormatter.format(plan.guard)
                body = plan.goals.joinToString("; ") { termFormatter.format(it.value) }
            }
            println("+!$trigger : $guard <- $body")
        }
    }

    fun String.capitalize() = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

    fun String.dropNumbersFromWords() = this.replace(Regex("(?<=\\w)\\d+"), "")

    fun Belief.removeSource(): Struct = this.rule.head.removeSource()

    fun Struct.removeSource(): Struct = removeFirst()

    fun String.removeSource(): Struct = Atom.of(this).removeFirst()

    fun Struct.removeFirst(): Struct =
        if (this.arity >= 1) {
            this.setArgs(this.args.drop(1))
        } else {
            this
        }

    fun List<Struct>.toLeftNestedAnd(): Struct? {
        return when {
            isEmpty() -> Truth.TRUE
            size == 1 -> first()
            else -> {
                var result = first()
                for (i in 1 until size) {
                    result = Struct.of("&", result, this[i])
                }
                result
            }
        }
    }
}
