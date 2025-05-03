package it.unibo.jakta.agents.bdi.visitors

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

class GuardFlattenerVisitor : DefaultTermVisitor<List<Struct>>() {
    override fun defaultValue(term: Term): List<Struct> = listOf(term.castToStruct())

    override fun visitStruct(term: Struct): List<Struct> {
        if (term.functor == "&" && term.arity == 2) {
            val leftTerms = term.args[0].accept(this)
            val rightTerms = term.args[1].accept(this)
            return leftTerms + rightTerms
        }
        return listOf(term)
    }

    companion object {
        fun Struct.flattenAnd(): List<Struct> = this.accept(GuardFlattenerVisitor())
    }
}
