package it.unibo.jakta.agents.bdi.visitors

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

class SourceWrapperVisitor(private val source: Term = Belief.SOURCE_SELF) : DefaultTermVisitor<Term>() {

    private val specialOperators = setOf("~", "\\=", "=", "==", "\\==", "<", ">", "=<", ">=")

    override fun defaultValue(term: Term): Term = term

    override fun visitStruct(term: Struct): Term {
        return when (term.functor) {
            in specialOperators -> {
                val newArgs = term.args.map { arg ->
                    // If the argument is a simple atom (not another special operator), do not wrap it
                    // If it's a complex structure or another special operator, process recursively
                    arg as? Atom ?: arg.accept(this)
                }.toList()
                term.setArgs(newArgs)
            }
            "&" -> {
                // For AND, process both sides and maintain the AND structure
                val left = term.args[0].accept(this)
                val right = term.args[1].accept(this)
                Struct.of("&", left, right)
            }
            "|" -> {
                // For OR, process both sides and maintain the OR structure
                val left = term.args[0].accept(this)
                val right = term.args[1].accept(this)
                Struct.of("|", left, right)
            }
            else -> {
                // For normal predicates, wrap them with the source
                Belief.wrap(term, wrappingTag = source).rule.head
            }
        }
    }

    companion object {
        /**
         * Wrap a term with the given source but skip special operators like ~, \=, etc.
         *
         * @param term The term to wrap
         * @param source The source to use for wrapping, defaults to SOURCE_SELF
         * @return A new term with appropriate parts wrapped
         */
        fun wrapSelectively(term: Term, source: Term = Belief.SOURCE_SELF): Term {
            return term.accept(SourceWrapperVisitor(source))
        }
    }
}
