package it.unibo.jakta.agents.bdi.visitors

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

class SourceAnonymizerVisitor {

    /**
     * Visits a term and returns a transformed version with source terms anonymized.
     */
    fun visit(term: Term): Term {
        return when (term) {
            is Struct -> visitStruct(term)
            else -> term
        }
    }

    /**
     * Visits a struct and returns a transformed version with source terms anonymized.
     */
    private fun visitStruct(struct: Struct): Struct {
        if (struct.functor == "source" && struct.arity == 1) {
            return Struct.of("source", Var.anonymous())
        }

        val transformedArgs = struct.args.map { visit(it) }
        return struct.setArgs(transformedArgs)
    }
}
