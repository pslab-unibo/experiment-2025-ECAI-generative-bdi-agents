package it.unibo.jakta.agents.bdi.plans.feedback

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

class GuardFlatteningVisitor : DefaultTermVisitor<List<Term>>() {
    override fun defaultValue(term: Term): List<Term> = listOf(term)

    override fun visitStruct(term: Struct): List<Term> {
        if (term.functor == "&" && term.arity == 2) {
            val leftTerms = term.args[0].accept(this)
            val rightTerms = term.args[1].accept(this)
            return leftTerms + rightTerms
        }
        return listOf(term)
    }

    companion object {
        fun Term.flattenAnd(): List<Term> = this.accept(GuardFlatteningVisitor())
    }
}
