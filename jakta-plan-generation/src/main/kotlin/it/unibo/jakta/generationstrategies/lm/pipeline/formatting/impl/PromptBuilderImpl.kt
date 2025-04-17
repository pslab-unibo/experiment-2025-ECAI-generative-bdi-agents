package it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptFormatter
import kotlin.apply
import kotlin.text.appendLine

class PromptBuilderImpl(
    override val promptPath: String?,
    override val remarks: List<Remark>,
    override val promptFormatter: PromptFormatter,
) : PromptBuilder {

    val systemPrompt = promptPath?.let { readResourceFile(it) } ?: "You are a helpful AI assistant."

    override fun buildPrompt(
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        plans: List<Plan>?,
        goal: String,
    ): List<ChatMessage> {
        val goalPrompt = buildGoalPrompt(
            remarks,
            internalActions,
            externalActions,
            beliefs,
            plans,
            goal,
        )

        return listOf(
            ChatMessage(
                role = ChatRole.System,
                content = systemPrompt,
            ),
            ChatMessage(
                role = ChatRole.User,
                content = goalPrompt,
            ),
        )
    }

    private fun buildGoalPrompt(
        remarks: List<Remark>,
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        plans: List<Plan>?,
        goal: String,
    ): String {
        val remarks = remarks.joinToString("\n") { it.value }
        val internalActions = promptFormatter.formatActions(internalActions)
        val externalActions = promptFormatter.formatActions(externalActions)
        val beliefs = beliefs?.let { promptFormatter.formatBeliefs(it) }.orEmpty()
        val planLibrary = plans
            ?.filter { it.trigger is AchievementGoalInvocation }
            ?.let { promptFormatter.formatPlans(it) }
            .orEmpty()

        return getGoalPromptString(
            remarks,
            internalActions,
            externalActions,
            beliefs,
            planLibrary,
            goal,
        )
    }

    companion object {
        fun readResourceFile(resourcePath: String): String? {
            val classLoader = object {}.javaClass.enclosingClass?.classLoader ?: ClassLoader.getSystemClassLoader()
            val inputStream = classLoader.getResourceAsStream(resourcePath) ?: return null
            return inputStream.bufferedReader().use { it.readText() }
        }

        fun getGoalPromptString(
            remarks: String,
            internalActions: String,
            externalActions: String,
            beliefs: String,
            planLibrary: String,
            goal: String,
        ) = StringBuilder().apply {
            appendLine("I want to solve this problem:")

            if (!beliefs.isBlank()) {
                appendLine()
                appendLine("Current state:")
                appendLine(beliefs)
            }

            if (goal.isNotBlank()) {
                appendLine()
                appendLine("Goal state:")
                appendLine(goal)
            }

            appendLine("Tell me what your next move would be and I’ll tell you if it is valid.")
            appendLine("These are the moves at your disposal:")

            if (!internalActions.isBlank()) {
                appendLine()
                appendLine(internalActions)
            }

            if (!externalActions.isBlank()) {
                appendLine()
                appendLine(externalActions)
            }

            if (!planLibrary.isBlank()) {
                appendLine()
                appendLine(planLibrary)
            }

            if (!remarks.isBlank()) {
                appendLine(remarks)
            }
        }.toString()
            .lines()
            .joinToString("\n") { it.trimStart() }
    }
}
