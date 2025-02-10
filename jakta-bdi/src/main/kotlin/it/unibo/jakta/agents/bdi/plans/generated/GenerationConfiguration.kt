package it.unibo.jakta.agents.bdi.plans.generated

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.generationstrategies.GenerationStrategy
import it.unibo.jakta.llm.Remark

interface GenerationConfiguration {
    val generate: Boolean
    val genStrategy: GenerationStrategy?
    val goal: String?
    val remarks: List<Remark>
    val actions: List<Action<*, *, *>>

    companion object {
        fun of(
            generate: Boolean = false,
            generationStrategy: GenerationStrategy? = null,
            remarks: List<Remark> = emptyList(),
            actions: List<Action<*, *, *>> = emptyList(),
        ): GenerationConfiguration = GenerationConfigurationImpl(
            generate,
            generationStrategy,
            remarks,
            actions,
        )
    }

    fun withGoal(goal: String): GenerationConfiguration
}
