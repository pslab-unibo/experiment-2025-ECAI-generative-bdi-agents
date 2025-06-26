package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.formatting

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.engine.actions.ExternalAction
import it.unibo.jakta.agents.bdi.engine.context.AgentContext
import it.unibo.jakta.agents.bdi.engine.goals.GeneratePlan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.Remark
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.AgentContextProperties
import it.unibo.jakta.agents.bdi.generationstrategies.lm.dsl.PromptScope
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.ContextFilter.Companion.applyAllTo
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.filtering.ExtendedAgentContext

interface PromptBuilder {
    fun build(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction> = emptyList(),
        contextFilters: List<ContextFilter> = emptyList(),
        remarks: Iterable<Remark> = emptyList(),
    ): ChatMessage

    companion object {
        fun <T> formatAsBulletList(
            items: Iterable<T>,
            formatter: (T) -> String?,
        ): String? {
            val formattedItems =
                items
                    .mapNotNull(formatter)
                    .filter { it.isNotBlank() }

            return if (formattedItems.isNotEmpty()) {
                formattedItems.joinToString("\n") { "- $it" }
            } else {
                null
            }
        }

        fun prompt(block: PromptScope.(AgentContextProperties) -> Unit) =
            object : PromptBuilder {
                override fun build(
                    initialGoal: GeneratePlan,
                    context: AgentContext,
                    externalActions: List<ExternalAction>,
                    contextFilters: List<ContextFilter>,
                    remarks: Iterable<Remark>,
                ): ChatMessage {
                    val extendedContext = ExtendedAgentContext(initialGoal, context, externalActions)
                    val filteredContext = contextFilters.applyAllTo(extendedContext)

                    val filteredInternalActions =
                        filteredContext.context.internalActions.values
                            .toList()
                    val filteredExternalActions = filteredContext.externalActions
                    val actualBeliefs = filteredContext.context.beliefBase
                    val admissibleBeliefs = filteredContext.context.admissibleBeliefs
                    val admissibleGoals = filteredContext.context.admissibleGoals
                    val actualGoals = filteredContext.context.planLibrary.plans

                    val properties =
                        AgentContextProperties(
                            filteredInternalActions,
                            filteredExternalActions,
                            actualBeliefs,
                            admissibleBeliefs,
                            admissibleGoals,
                            actualGoals,
                            initialGoal,
                            remarks,
                        )

                    val scope = PromptScope()
                    block.invoke(scope, properties)
                    return scope.buildAsMessage()
                }
            }
    }
}
