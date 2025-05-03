package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.dsl.AgentContextProperties
import it.unibo.jakta.generationstrategies.lm.dsl.PromptScope
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.ContextFilter
import it.unibo.jakta.generationstrategies.lm.pipeline.filtering.ExtendedAgentContext

interface PromptBuilder {
    fun build(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction> = emptyList(),
        contextFilter: ContextFilter? = null,
        remarks: Iterable<Remark> = emptyList(),
    ): ChatMessage

    companion object {
        fun <T> formatAsBulletList(
            items: Iterable<T>,
            formatter: (Iterable<T>) -> List<String>,
        ): String? {
            val res = formatter(items)
            return if (res.isNotEmpty()) {
                res.filter { it.isNotBlank() }.joinToString(separator = "\n") { "- $it" }
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
                    contextFilter: ContextFilter?,
                    remarks: Iterable<Remark>,
                ): ChatMessage {
                    val extendedContext = ExtendedAgentContext(initialGoal, context, externalActions)
                    val filteredContext = contextFilter?.filter(extendedContext) ?: extendedContext

                    val filteredInternalActions = filteredContext.context.internalActions.values.toList()
                    val filteredExternalActions = filteredContext.externalActions
                    val actualBeliefs = filteredContext.context.beliefBase
                    val admissibleBeliefs = filteredContext.context.admissibleBeliefs
                    val admissibleGoals = filteredContext.context.admissibleGoals
                    val actualGoals = filteredContext.context.planLibrary.plans

                    val properties = AgentContextProperties(
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
