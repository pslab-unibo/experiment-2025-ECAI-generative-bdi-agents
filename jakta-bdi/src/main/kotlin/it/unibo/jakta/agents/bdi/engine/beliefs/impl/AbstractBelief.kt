package it.unibo.jakta.agents.bdi.engine.beliefs.impl

import it.unibo.jakta.agents.bdi.engine.beliefs.Belief
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlinx.serialization.Serializable

@Serializable
internal abstract class AbstractBelief : Belief {
    override fun applySubstitution(substitution: Substitution): Belief =
        BeliefImpl(rule.apply(substitution).castToRule(), purpose)

    override fun hashCode(): Int = rule.hashCode()

    override fun toString(): String {
        val escaped = Struct.escapeFunctorIfNecessary(rule.head.functor)
        val quoted = Struct.enquoteFunctorIfNecessary(escaped)
        return "$quoted[${rule.head.args.first()}]" +
            (if (rule.head.arity > 1) "(${rule.head.args.drop(1).joinToString(", ")})" else "") +
            (if (!rule.body.isTrue) " ${rule.functor} ${rule.body}" else "") +
            if (purpose != null) ": $purpose" else "."
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractBelief) return false
        return rule == other.rule
    }
}
