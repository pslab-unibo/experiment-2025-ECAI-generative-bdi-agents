package it.unibo.jakta.agents.bdi.dsl.plans

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.dsl.Builder
import it.unibo.jakta.agents.bdi.generationstrategies.GenerationStrategy
import it.unibo.jakta.agents.bdi.plans.generated.GenerationConfiguration
import it.unibo.jakta.llm.Remark

class PlanConfigScope : Builder<GenerationConfiguration> {

    /**
     * The [GenerationStrategy] that defines how to generate plans.
     */
    var generationStrategy: GenerationStrategy? = null

    /**
     * Whether to use the default generation strategy or not if one is available.
     * If a generation strategy is set in the scope, this parameter does not need
     * to be set explicitly.
     */
    var generate = false

    val remarks = mutableListOf<Remark>()

    val actions = mutableListOf<Action<*, *, *>>()

    /**
     * Handler for the addition of a remark to the prompt of an LLM.
     * @param remark the [String] that provides additional context in the prompt.
     */
    fun remark(remark: String) {
        remarks += Remark(remark)
    }

    fun remarks(vararg remark: String) {
        remarks.addAll(remark.map { Remark(it) })
    }

    /**
     * Handler for the addition of a tool to the prompt of an LLM.
     * @param action the [Action] that provides additional context in the prompt.
     */
    fun action(action: Action<*, *, *>) {
        actions += action
    }

    fun actions(vararg action: Action<*, *, *>) {
        actions.addAll(action)
    }

    override fun build(): GenerationConfiguration = GenerationConfiguration.of(
        generationStrategy != null,
        generationStrategy,
        remarks,
        actions,
    )
}
