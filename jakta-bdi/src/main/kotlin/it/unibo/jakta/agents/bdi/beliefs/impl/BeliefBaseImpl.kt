package it.unibo.jakta.agents.bdi.beliefs.impl

import it.unibo.jakta.agents.bdi.Jakta.parseClause
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.beliefs.BeliefUpdate
import it.unibo.jakta.agents.bdi.beliefs.RetrieveResult
import it.unibo.jakta.agents.bdi.visitors.SourceAnonymizerVisitor
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.TrackVariables
import it.unibo.tuprolog.solve.flags.Unknown
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.unify.Unificator

internal class BeliefBaseImpl private constructor(
    private val beliefs: ClauseMultiSet,
    private val purposeMap: Map<Rule, String?>,
) : BeliefBase {

    constructor() : this(ClauseMultiSet.empty(Unificator.default), emptyMap())

    override fun add(belief: Belief) = when (beliefs.count(belief.rule)) {
        // There's no Belief that unifies the param inside the MultiSet, so it's inserted
        0L -> RetrieveResult(
            listOf(BeliefUpdate.addition(belief)),
            BeliefBaseImpl(
                beliefs.add(belief.rule),
                purposeMap + (belief.rule to belief.purpose),
            ),
        )
        // There are Beliefs that unify the param, so the belief it's not inserted
        else -> RetrieveResult(emptyList(), this)
    }

    override fun addAll(beliefs: BeliefBase): RetrieveResult {
        var addedBeliefs = emptyList<BeliefUpdate>()
        var bb: BeliefBase = this
        beliefs.forEach {
            val rr = bb.add(it)
            addedBeliefs = addedBeliefs + rr.modifiedBeliefs
            bb = rr.updatedBeliefBase
        }
        return RetrieveResult(addedBeliefs, bb)
    }

    override fun remove(belief: Belief): RetrieveResult {
        return if (beliefs.count(belief.rule) > 0) {
            val foundBelief = firstOrNull { it == belief }
            if (foundBelief != null) {
                RetrieveResult(
                    listOf(BeliefUpdate.removal(foundBelief)),
                    BeliefBase.of(filter { it != foundBelief }),
                )
            } else {
                // The belief to remove is not found
                RetrieveResult(listOf(), this)
            }
        } else {
            RetrieveResult(listOf(), this)
        }
    }

    override fun update(belief: Belief): RetrieveResult {
        val element = beliefs.find { it.head?.functor == belief.rule.head.functor }
        return if (element != null) {
            var retrieveResult = remove(Belief.from(element.head!!))
            retrieveResult = retrieveResult.updatedBeliefBase.add(belief)
            RetrieveResult(listOf(), retrieveResult.updatedBeliefBase)
        } else {
            RetrieveResult(listOf(), this)
        }
    }

    override fun removeAll(beliefs: BeliefBase): RetrieveResult {
        var removedBeliefs = emptyList<BeliefUpdate>()
        var bb: BeliefBase = this
        beliefs.forEach {
            val rr = bb.remove(it)
            removedBeliefs = removedBeliefs + rr.modifiedBeliefs
            bb = rr.updatedBeliefBase
        }
        return RetrieveResult(removedBeliefs, bb)
    }

    override fun iterator(): Iterator<Belief> =
        beliefs.filterIsInstance<Rule>().map {
            Belief.from(it, purposeMap[it])
        }.iterator()

    override fun solveAll(struct: Struct, ignoreSource: Boolean): Sequence<Solution> =
        if (ignoreSource) {
            createSolver().solve(createAllSourcesMatchingStruct(struct))
        } else {
            createSolver().solve(struct)
        }

    override fun solve(struct: Struct, ignoreSource: Boolean): Solution =
        if (ignoreSource) {
            createSolver().solveOnce(createAllSourcesMatchingStruct(struct))
        } else {
            createSolver().solveOnce(struct)
        }

    private fun createAllSourcesMatchingStruct(struct: Struct): Struct {
        val visitor = SourceAnonymizerVisitor()
        return visitor.visit(struct).castToStruct()
    }

    private fun createSolver(): Solver =
        Solver.prolog.newBuilder()
            .flag(Unknown, Unknown.FAIL)
            .staticKb(operatorExtension + Theory.of(beliefs))
            .flag(TrackVariables) { ON }
            .build()

    override fun solve(belief: Belief, ignoreSource: Boolean): Solution = solve(belief.rule.head)

    override fun solveAll(belief: Belief, ignoreSource: Boolean): Sequence<Solution> = solveAll(belief.rule.head)

    override fun toString(): String =
        beliefs.joinToString { rule ->
            val purpose = purposeMap[rule.castToRule()]
            Belief.from(rule.castToRule(), purpose).toString()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BeliefBaseImpl

        if (beliefs != other.beliefs) return false
        if (purposeMap != other.purposeMap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = beliefs.hashCode()
        result = 31 * result + purposeMap.hashCode()
        return result
    }

    companion object {
        private val operatorExtension = Theory.of(
            parseClause("&(A, B) :- A, B"),
            parseClause("|(A, _) :- A"),
            parseClause("|(_, B) :- B"),
            parseClause("~(X) :- not(X)"),
        )
    }
}
