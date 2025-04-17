package it.unibo.jakta.agents.bdi.dsl.beliefs

import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleStruct
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleStructs
import it.unibo.jakta.agents.bdi.parsing.templates.LiteratePrologTemplate
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.jakta.JaktaLogicProgrammingScope

/**
 * Builder for Jakta Agents's [BeliefBase].
 */
class BeliefsScope(
    private val templates: List<LiteratePrologTemplate> = emptyList(),
    private val lp: JaktaLogicProgrammingScope = JaktaLogicProgrammingScope.empty(),
) : Builder<BeliefBase>, JaktaLogicProgrammingScope by lp {

    var typeCheckRule: ((String) -> Rule)? = null
    private val beliefs = mutableListOf<Belief>()

    /**
     * Handler for the addition of a fact [Belief] into the agent's [BeliefBase].
     * @param struct the [Struct] that represents the [Belief].
     */
    fun fact(
        struct: Struct,
        template: LiteratePrologTemplate? = null,
        slotValues: List<Pair<String, String>> = emptyList(),
    ) = beliefs.add(createBelief(struct, template, slotValues))

    /**
     * Handler for the addition of a fact [Belief] into the agent's [BeliefBase].
     * @param function executed in the [JaktaLogicProgrammingScope] context to describe agent's [Belief].
     */
    override fun fact(function: JaktaLogicProgrammingScope.() -> Any): Fact =
        lp.fact { function() }.also { fact(it.head) }

    /**
     * Handler for the addition of a fact [Belief] into the agent's [BeliefBase].
     * @param atom the [String] representing the [Atom] the agent is going to believe.
     */
    fun fact(atom: String): Boolean {
        val parsedFact: Struct? = tangleStructs(atom, templates).firstOrNull()
        return if (parsedFact != null) {
            fact(parsedFact)
        } else {
            fact(atomOf(atom))
        }
    }

    /**
     * Handler for the addition of a rule [Belief] into the agent's [BeliefBase].
     * @param function executed in the [JaktaLogicProgrammingScope] context to describe agent's [Belief].
     */
    override fun rule(function: JaktaLogicProgrammingScope.() -> Any): Rule =
        lp.rule(function).also { rule(it) }

    /**
     * Handler for the addition of a rule [Belief] into the agent's [BeliefBase].
     * @param rule the [Rule] the agent is going to believe.
     */
    fun rule(rule: Rule) {
        val freshRule = rule.freshCopy()
        val belief: Belief = Belief.wrap(freshRule.head, freshRule.bodyItems, wrappingTag = Belief.SOURCE_SELF)
        beliefs.add(belief)
    }

    fun createBelief(
        struct: Struct,
        template: LiteratePrologTemplate? = null,
        slotValues: List<Pair<String, String>> = emptyList(),
    ): Belief =
        Belief.wrap(
            struct.freshCopy(),
            wrappingTag = Belief.SOURCE_SELF,
            template = template,
            slotValues = slotValues,
        )

    override fun build(): BeliefBase {
        val (toKeep, toConvert) = beliefs.partition {
            tangleStruct(it.rule.head.toString(), templates) == null
        }

        val converted = toConvert.map {
            createBelief(tangleStruct(it.rule.head.toString(), templates)!!)
        }

        return BeliefBase.of(typeCheckRule, toKeep + converted)
    }
}
