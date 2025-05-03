package it.unibo.jakta.agents.bdi.visitors

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.visitors.DefaultTermVisitor

class SourceWrapperVisitor(private val source: Term = Belief.SOURCE_SELF) : DefaultTermVisitor<Term>() {

    override fun defaultValue(term: Term): Term = term

    override fun visitStruct(term: Struct): Term {
        // Don't wrap negation, inequality, or other special operators
        return when (term.functor) {
            "~", "\\=", "=", "==", "\\==", "<", ">", "=<", ">=" -> {
                // For these operators, we process their arguments but keep the operator unwrapped
                val newArgs = term.args.map { it.accept(this) }.toList()
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
         * Wrap a term with source information, but skip special operators like ~, \=, etc.
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
