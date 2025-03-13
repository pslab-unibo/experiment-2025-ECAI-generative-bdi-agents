package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.pipeline.formatter.PromptFormatter
import kotlin.apply
import kotlin.text.appendLine

class PromptGeneratorImpl(
    override val remarks: List<Remark>,
    override val promptFormatter: PromptFormatter,
) : PromptGenerator {

    override fun buildPrompt(
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        plans: List<Plan>?,
        generatedPlan: GeneratedPlan,
    ): List<ChatMessage> {
        val goal = (
            generatedPlan.literateTrigger
                ?: generatedPlan.trigger.value.functor
            ).trimIndent()

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
                content = system,
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
        val system =
            """
            You are a helpful AI assistant.
            Your objective is to solve problems by providing only the next optimal step.
            Do not recap previous steps or predict future states - focus solely on the current recommendation.
            Wait for user feedback before proceeding.
            """.trimIndent()

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
                appendLine(
                    """
                Current state:
                $beliefs
                    """.trimIndent(),
                )
            }

            if (goal.isNotBlank()) {
                appendLine()
                appendLine(
                    """
                Goal state:
                $goal
                    """.trimIndent(),
                )
            }

            appendLine(
                """
                
                Tell me what your next move would be and I’ll tell you if it is valid.
                These are the moves at your disposal:
                """.trimIndent(),
            )

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
