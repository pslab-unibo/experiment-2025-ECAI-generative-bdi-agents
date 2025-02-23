package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.generationstrategies.lm.Remark

class PromptGeneratorImpl(
    override val remarks: List<Remark> = emptyList(),
) : PromptGenerator {

    override fun buildPrompt(
        internalActions: List<InternalAction>?,
        externalActions: List<ExternalAction>?,
        beliefs: BeliefBase?,
        planLibrary: PlanLibrary?,
        goal: String,
    ): List<ChatMessage> {
        val remarks = if (remarks.isEmpty()) {
            "None."
        } else {
            remarks.joinToString("\n") { it.value }
        }

        val systemPrompt = buildSystemPrompt(
            internalActions,
            externalActions,
            beliefs,
            planLibrary,
            goal,
        )

        return listOf(
            ChatMessage(
                role = ChatRole.System,
                content = systemPrompt,
            ),
            ChatMessage(
                role = ChatRole.User,
                content = "content",
            ),
        )
    }

    fun buildSystemPrompt(
        internalActions: List<InternalAction>?,
        externalActions: List<ExternalAction>?,
        beliefs: BeliefBase?,
        planLibrary: PlanLibrary?,
        goal: String,
    ): String =
        """
        You are an helpful assistant.
        You have a set of tools at your disposal to answer the user's queries.
        
        Here is a description of the available tools:
        
        ```
        $internalActions
        ```
        
        For example, to get the weather for the city of Roma you must answer [weather("Roma", @weather)]
        Please use the square brackets, not the backticks.  
        """.trimIndent()
}
