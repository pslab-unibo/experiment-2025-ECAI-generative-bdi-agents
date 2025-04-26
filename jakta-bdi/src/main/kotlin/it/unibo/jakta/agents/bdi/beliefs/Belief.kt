package it.unibo.jakta.agents.bdi.beliefs

import it.unibo.jakta.agents.bdi.Documentable
import it.unibo.jakta.agents.bdi.beliefs.impl.BeliefImpl
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

interface Belief : Documentable {
    val rule: Rule

    fun applySubstitution(substitution: Substitution): Belief

    fun copy(
        rule: Rule = this.rule,
        purpose: String? = this.purpose,
    ): Belief

    companion object {
        val SOURCE_PERCEPT: Term = Struct.of("source", Atom.of("percept"))
        val SOURCE_SELF: Term = Struct.of("source", Atom.of("self"))
        val SOURCE_UNKNOWN: Term = Struct.of("source", Var.of("Source"))

        fun wrap(
            head: Struct,
            body: Iterable<Term> = emptyList(),
            wrappingTag: Term = SOURCE_UNKNOWN,
            purpose: String? = null,
        ): Belief {
            if (head.arity >= 1 && head[0].let { it is Struct && it.arity == 1 && it.functor == "source" }) {
                return BeliefImpl(Rule.of(head, body), purpose)
            }
            return BeliefImpl(Rule.of(head.addFirst(wrappingTag), body), purpose)
        }

        fun of(
            head: Struct,
            body: Iterable<Term>,
            isFromPerceptSource: Boolean,
            purpose: String? = null,
        ): Belief {
            val headArguments = (if (isFromPerceptSource) listOf(SOURCE_PERCEPT) else listOf(SOURCE_SELF)) + head.args
            return BeliefImpl(
                Rule.of(
                    Struct.of(head.functor, headArguments),
                    body,
                ),
                purpose,
            )
        }

        fun of(
            head: Struct,
            body: Iterable<Term>,
            from: String,
            purpose: String? = null,
        ): Belief {
            val headArguments = listOf(Struct.of("source", Atom.of(from))) + head.args
            return BeliefImpl(
                Rule.of(
                    Struct.of(head.functor, headArguments),
                    body,
                ),
                purpose,
            )
        }

        fun fromSelfSource(head: Struct, vararg body: Term): Belief =
            fromSelfSource(head, body.asIterable())

        fun fromSelfSource(head: Struct, body: Sequence<Term>): Belief =
            fromSelfSource(head, body.asIterable())

        fun fromSelfSource(head: Struct, body: Iterable<Term>): Belief =
            of(head, body, false)

        fun fromPerceptSource(head: Struct, vararg body: Term): Belief =
            fromPerceptSource(head, body.asIterable())

        fun fromPerceptSource(head: Struct, body: Sequence<Term>): Belief =
            fromPerceptSource(head, body.asIterable())

        fun fromPerceptSource(head: Struct, body: Iterable<Term>): Belief =
            of(head, body, true)

        fun fromMessageSource(from: String, head: Struct, vararg body: Term): Belief =
            fromMessageSource(from, head, body.asIterable())

        fun fromMessageSource(from: String, head: Struct, body: Sequence<Term>): Belief =
            fromMessageSource(from, head, body.asIterable())

        fun fromMessageSource(from: String, head: Struct, body: Iterable<Term>): Belief =
            of(head, body, from)

        fun from(rule: Rule, purpose: String? = null): Belief =
            if (rule.head.isBelief()) {
                BeliefImpl(rule, purpose)
            } else {
                throw IllegalArgumentException("The rule is not a belief: $rule")
            }

        fun from(struct: Struct, purpose: String? = null): Belief = from(Rule.of(struct), purpose)

        fun Struct.isBelief() =
            this.args.isNotEmpty() &&
                this.args.first() is Struct &&
                this.args.first().castToStruct().functor == "source"
    }
}
