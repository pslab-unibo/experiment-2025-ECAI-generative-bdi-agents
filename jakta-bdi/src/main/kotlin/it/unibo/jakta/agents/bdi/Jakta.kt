package it.unibo.jakta.agents.bdi

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.TermFormatter
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

    val formatter: TermFormatter = TermFormatter.prettyExpressions(
        operatorSet = OperatorSet.DEFAULT + Jakta.operators,
    )

    private val parser = TermParser.withOperators(OperatorSet.DEFAULT + operators)
    fun parseStruct(string: String): Struct = parser.parseStruct(string)
    fun parseClause(string: String): Clause = parser.parseClause(string)

    fun printAslSyntax(agent: Agent, prettyFormatted: Boolean = true) {
        println("% ${agent.name}")
        for (belief in agent.context.beliefBase) {
            if (prettyFormatted) {
                println(formatter.format(belief.rule))
            } else {
                println(belief.rule)
            }
        }
        for (plan in agent.context.planLibrary.plans) {
            var trigger = plan.trigger.value.toString()
            var guard = plan.guard.toString()
            var body = plan.goals.joinToString("; ") { it.value.toString() }
            if (prettyFormatted) {
                trigger = formatter.format(plan.trigger.value)
                guard = formatter.format(plan.guard)
                body = plan.goals.joinToString("; ") { formatter.format(it.value) }
            }
            println("+!$trigger : $guard <- $body")
        }
    }

    fun String.capitalize() = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }

    /**
     * Creates a novel [Struct] which is a copy of the current one, except that
     * it has one argument less. The removed argument is the first of the old
     * [Struct].
     *
     * @return a new [Struct], whose functor is equals to the current one,
     * whose arity is less than the current one and which has not the first
     * argument of the old [Struct]
     */
    fun Struct.removeFirst(condition: ((Struct) -> Boolean)? = null): Struct =
        if (this.arity >= 1) {
            if (condition != null && condition(this)) {
                Struct.of(this.functor, this.args.subList(1, this.args.count()))
            } else if (condition == null) {
                Struct.of(this.functor, this.args.subList(1, this.args.count()))
            } else {
                this
            }
        } else {
            this
        }

    fun Struct.removeSource(): Struct =
        this.removeFirst {
            this[0].let { it is Struct && it.arity == 1 && it.functor == "source" }
        }
}
