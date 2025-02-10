package it.unibo.jakta.agents.bdi.plans.generated

import it.unibo.jakta.agents.bdi.actions.Action
import it.unibo.jakta.agents.bdi.generationstrategies.GenerationStrategy
import it.unibo.jakta.llm.Remark

data class GenerationConfigurationImpl(
    override val generate: Boolean,
    override val genStrategy: GenerationStrategy?,
    override val remarks: List<Remark>,
    override val actions: List<Action<*, *, *>>,
    override val goal: String? = null,
) : GenerationConfiguration {
    override fun withGoal(goal: String): GenerationConfiguration =
        GenerationConfigurationImpl(
            generate,
            genStrategy,
            remarks,
            actions,
            goal,
        )
}
