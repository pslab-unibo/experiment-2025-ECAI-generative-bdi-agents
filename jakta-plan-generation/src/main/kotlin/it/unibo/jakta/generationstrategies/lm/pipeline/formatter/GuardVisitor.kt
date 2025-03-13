package it.unibo.jakta.generationstrategies.lm.pipeline.formatter

import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.LiteratePrologParser.wrapWithDelimiters
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

class GuardVisitor : DefaultTermVisitor<String>() {
    override fun defaultValue(term: Term): String = term.toString()

    override fun visitVar(term: Var): String = "@${term.name.lowercase()}"

    override fun visitAtom(term: Atom): String = term.value

    override fun visitStruct(term: Struct): String {
        val t = term.removeSource()
        if (t.functor == "&" && t.arity == 2) {
            val left = t.args[0].accept(this)
            val right = t.args[1].accept(this)
            return "$left and $right"
        }

        val argsStr = t.args.joinToString(", ") { it.accept(this) }
        return "${t.functor}($argsStr)".wrapWithDelimiters()
    }

    override fun visitNumeric(term: Numeric): String = term.intValue.toString()
}

fun Term.toReadableString(): String = this.accept(GuardVisitor())

fun List<Term>.toReadableString(): String = this.joinToString(", ") { it.toReadableString() }
