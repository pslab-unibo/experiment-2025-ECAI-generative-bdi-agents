package it.unibo.jakta.generationstrategies.lm.pipeline

import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import it.unibo.jakta.agents.bdi.Jakta
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.actions.InternalAction
import it.unibo.jakta.agents.bdi.beliefs.BeliefBase
import it.unibo.jakta.agents.bdi.plans.GeneratedPlan
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.Plan
import it.unibo.jakta.agents.bdi.plans.PlanLibrary
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.tuprolog.core.TermFormatter
import it.unibo.tuprolog.core.operators.OperatorSet
import kotlin.apply

class PromptGeneratorImpl(
    override val remarks: List<Remark> = emptyList(),
) : PromptGenerator {

    override fun buildPrompt(
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        planLibrary: PlanLibrary?,
        generatedPlan: GeneratedPlan,
    ): List<ChatMessage> {
        val systemPrompt = buildSystemPrompt(
            remarks,
            internalActions,
            externalActions,
            beliefs,
            planLibrary?.plans?.minus(generatedPlan),
        )
        val goal = (
            generatedPlan.literateTrigger
                ?: generatedPlan.trigger.value.functor
            ).trimIndent()
        val goalPrompt =
            """
            Your task is to build a plan to complete the following goal:
            
            ```
            $goal
            ```
            """.lines().joinToString("\n") {
                it.trimStart()
            }

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

    private fun buildSystemPrompt(
        remarks: List<Remark>,
        internalActions: List<InternalAction>,
        externalActions: List<ExternalAction>,
        beliefs: BeliefBase?,
        plans: List<Plan>?,
    ): String {
        val remarks = if (remarks.isEmpty()) {
            ""
        } else {
            remarks.joinToString("\n") { it.value }
        }

        val internalActions = if (internalActions.isEmpty()) {
            ""
        } else {
            internalActions.joinToString("\n") { it.signature.description.trimIndent() }
        }

        val externalActions = if (externalActions.isEmpty()) {
            ""
        } else {
            externalActions.joinToString("\n") { it.signature.description.trimIndent() }
        }

        val beliefs = beliefs?.joinToString("\n") {
            val formatter = TermFormatter.prettyExpressions(operatorSet = OperatorSet.DEFAULT + Jakta.operators)
            "[${formatter.format(it.rule.head)}] is ${it.rule.body}"
        } ?: ""

        val planLibrary = plans?.joinToString("\n") {
            if (it is LiteratePlan) {
                formatLiteratePlan(it)
            } else {
                formatPlan(it)
            }
        }?.trimEnd() ?: ""

        return getSystemPromptString(
            remarks,
            internalActions,
            externalActions,
            beliefs,
            planLibrary,
        )
    }

    companion object {

        const val HEAD =
            """
            You are an expert assistant who creates plans for Belief-Desire-Intention (BDI) agents. 
            A BDI agent has beliefs, desires, intentions, plans and actions:
             
            - Beliefs: a set of facts and rules representing an agent’s epistemic memory, possibly describing its knowledge about the world, itself, and other agents.
            - Desires: a set of goals, representing (possibly partial) descriptions of desirable states of the world the agent is willing to achieve, test, or maintain.
            - Intentions: a set of tasks the agent is currently committed to, in order to satisfy some of its desires.
            - Plans: a set of recipes representing the agent’s procedural memory, hence encoding the know-how about achieving a given intention under certain conditions.
            - Actions: the operations that the agent can perform to act on the environment.
             
            Your task is to write clear, executable plans using the following predicates:

            [achieve(*)]: Execute a plan if its preconditions are true.

            Guidelines:
            
            - Define the Goal: State the agent's goal clearly.
            - Break Down the Plan: Use natural language to describe the sequence of actions and beliefs.
            - Use Predicates: Embed predicates like [achieve(*)], etc., to define the plan's logic.
            - Preconditions: Any Prolog fragment not in the above formats is treated as a precondition. All preconditions are consolidated into an and struct and unified with the belief base.
            """

        const val TAIL =
            """
            Example Plan:
            ```
            Goal: Deliver a package to [@destination]
            
            Preconditions:
            
            [has_package(@package)]: Check if the agent has the package.
            [location(@current)]: Verify the agent's current location.
            
            Steps:
            
            [achieve(navigate(@current, @destination))]: Navigate to the destination.
            [achieve(deliver(@package))]: Deliver the package.
            ```
            
            Your task is to write a plan for a BDI agent to achieve the given goal, using the above format.
            
            Ensure the plan is clear, executable, and includes all necessary preconditions.
            Take your time to think and perform your task at the best of your capabilities.
            Take a deep breath and go on!"""

        fun getSystemPromptString(
            remarks: String,
            internalActions: String,
            externalActions: String,
            beliefs: String,
            planLibrary: String,
        ): String = StringBuilder().apply {
            appendLine(HEAD)

            val toolIntro =
                if (!(internalActions.isEmpty() && externalActions.isEmpty()) && planLibrary.isNotEmpty()) {
                    "You have a set of actions and plans at your disposal to achieve the goal."
                } else if (!(internalActions.isEmpty() && externalActions.isEmpty()) && planLibrary.isEmpty()) {
                    "You have a set of actions at your disposal to achieve the goal."
                } else if (internalActions.isEmpty() && externalActions.isEmpty() && planLibrary.isNotEmpty()) {
                    "You have a set of plans at your disposal to achieve the goal."
                } else {
                    null
                }

            toolIntro?.let { appendLine(it) }

            val actions =
                if (internalActions.isNotEmpty() && externalActions.isNotEmpty()) {
                    """
                    Available actions [execute(*)]:
                    ```
                    $internalActions
                    
                    $externalActions
                    ```"""
                } else if (internalActions.isEmpty() && externalActions.isNotEmpty()) {
                    """
                    Available actions [execute(*)]:
                    ```
                    ${externalActions.trimIndent()}
                    ```"""
                } else if (internalActions.isNotEmpty() && externalActions.isEmpty()) {
                    """
                    Available actions [execute(*)]:
                    ```
                    ${internalActions.trimIndent()}
                    ```"""
                } else {
                    null
                }

            actions?.let { appendLine(it) }

            val plans =
                if (planLibrary.isNotEmpty()) {
                    """            
                    Available plans [achieve(*)]:
                    $planLibrary
                    """
                } else {
                    null
                }

            plans?.let { appendLine(it) }

            val beliefsRemarksIntro =
                if (beliefs.isNotEmpty() && remarks.isNotEmpty()) {
                    "Use the current beliefs and the given remarks to guide your thinking."
                } else if (beliefs.isEmpty() && remarks.isNotEmpty()) {
                    "Use the given remarks to guide your thinking."
                } else if (beliefs.isNotEmpty() && remarks.isEmpty()) {
                    "Use the current beliefs to guide your thinking."
                } else {
                    null
                }

            beliefsRemarksIntro?.let { appendLine(it) }

            val beliefs =
                if (beliefs.isNotEmpty()) {
                    """
                    Current beliefs:
                    ```
                    $beliefs
                    ```"""
                } else {
                    null
                }

            beliefs?.let { appendLine(it) }

            val remarks =
                if (remarks.isNotEmpty()) {
                    """
                    Remarks:
                    ```
                    $remarks
                    ```"""
                } else {
                    null
                }

            remarks?.let { appendLine(it) }

            appendLine(TAIL)
        }.toString().lines().joinToString("\n") { it.trimStart() }

        fun formatLiteratePlan(litPlan: LiteratePlan): String {
            var trigger = litPlan.literateTrigger?.trimIndent()
            var guard = litPlan.literateGuard?.trimIndent()
            var body = litPlan.literateGoals?.trimIndent()

            return """```
                Goal: $trigger
                
                Preconditions:
                
                $guard
                
                Steps:
                
                $body
                ```
            """
        }

        fun formatPlan(plan: Plan): String {
            var trigger = plan.trigger.value.toString()
            var guard = plan.guard.toString()
            var body = plan.goals.joinToString(",\n") { it.value.toString() }

            val formatter = TermFormatter.readable(operators = OperatorSet.DEFAULT + Jakta.operators)
            trigger = "[${formatter.format(plan.trigger.value)}]"
            guard = "[${formatter.format(plan.guard)}]"
            body = plan.goals.joinToString(",\n") { s -> "[${formatter.format(s.value)}]" }

            val formattedPlan = """```
                Goal: $trigger
                
                $body
                
                Preconditions:
                
                $guard
                ```
            """
            return formatVariables(formattedPlan)
        }

        fun formatVariables(input: String): String {
            val regex = Regex("\\b[A-Z][A-Za-z]*\\b")
            return regex.replace(input) { matchResult ->
                "@${matchResult.value.lowercase()}"
            }
        }
    }
}
