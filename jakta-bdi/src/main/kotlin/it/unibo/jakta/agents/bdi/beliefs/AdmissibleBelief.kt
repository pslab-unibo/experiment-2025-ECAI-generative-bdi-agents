package it.unibo.jakta.agents.bdi.beliefs

import it.unibo.jakta.agents.bdi.beliefs.Belief.Companion.SOURCE_UNKNOWN
import it.unibo.jakta.agents.bdi.beliefs.impl.AdmissibleBeliefImpl
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

interface AdmissibleBelief : Belief {
    companion object {
        fun wrap(
            head: Struct,
            body: Iterable<Term> = emptyList(),
            wrappingTag: Term = SOURCE_UNKNOWN,
            purpose: String? = null,
        ): AdmissibleBelief {
            if (head.arity >= 1 && head[0].let { it is Struct && it.arity == 1 && it.functor == "source" }) {
                return AdmissibleBeliefImpl(Rule.of(head, body), purpose)
            }
            return AdmissibleBeliefImpl(Rule.of(head.addFirst(wrappingTag), body), purpose)
        }

        fun from(rule: Rule, purpose: String? = null): AdmissibleBelief {
            if (rule.head.args.isNotEmpty() &&
                rule.head.args.first() is Struct &&
                rule.head.args.first().castToStruct().functor == "source"
            ) {
                return AdmissibleBeliefImpl(rule, purpose)
            }
            throw IllegalArgumentException("The rule is not a belief: $rule")
        }
    }
}
