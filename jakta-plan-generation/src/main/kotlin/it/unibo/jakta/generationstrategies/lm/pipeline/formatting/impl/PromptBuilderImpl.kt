package it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl

import com.aallam.openai.api.chat.ChatMessage
import it.unibo.jakta.agents.bdi.Jakta.termFormatter
import it.unibo.jakta.agents.bdi.actions.ExternalAction
import it.unibo.jakta.agents.bdi.context.AgentContext
import it.unibo.jakta.agents.bdi.goals.GeneratePlan
import it.unibo.jakta.generationstrategies.lm.Remark
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.Formatters.actionsFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.Formatters.admissibleBeliefsFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.Formatters.admissibleGoalsFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.Formatters.beliefsFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.Formatters.planFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.Formatters.triggerFormatter
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.impl.PromptScope.Companion.prompt

class PromptBuilderImpl(
    override val remarks: List<Remark>,
    override val withSubgoals: Boolean,
) : PromptBuilder {

    override fun buildPrompt(
        initialGoal: GeneratePlan,
        context: AgentContext,
        externalActions: List<ExternalAction>,
    ): ChatMessage {
        val internalActions = context.internalActions.values.toList()
        val actualBeliefs = context.beliefBase
        val admissibleBeliefs = context.admissibleBeliefs
        val admissibleGoals = context.admissibleGoals
        val actualGoals = context.planLibrary.plans

        return prompt {
            section("Background") { fromFile("background.md") }

            section("Agent's internal state") {
                section("Beliefs") {
                    section("Admissible beliefs") {
                        fromFormatter(admissibleBeliefs) {
                            formatAsBulletList(it, admissibleBeliefsFormatter::format)
                        }
                    }

                    section("Actual beliefs") {
                        fromFormatter(actualBeliefs.asIterable().toList()) {
                            formatAsBulletList(it, beliefsFormatter::format)
                        }
                    }
                }

                section("Goals") {
                    section("Admissible goals") {
                        fromString("- ${termFormatter.format(initialGoal.value)}")

                        fromFormatter(admissibleGoals) {
                            formatAsBulletList(it, admissibleGoalsFormatter::format)
                        }
                    }

                    section("Actual goals") {
                        fromFormatter(actualGoals) { plans ->
                            if (withSubgoals) {
                                planFormatter.format(plans).joinToString(
                                    prefix = "\n",
                                    separator = "\n",
                                )
                            } else {
                                val triggers = plans.map { it.trigger }
                                formatAsBulletList(triggers, triggerFormatter::format)
                            }
                        }
                    }
                }

                section("Admissible actions") {
                    fromFormatter(internalActions + externalActions) {
                        formatAsBulletList(it, actionsFormatter::format)
                    }
                }
            }

            section("Remarks") {
                fromFormatter(remarks) {
                    formatAsBulletList(it) { remarks.map { r -> r.value } }
                }
            }

            section("Expected outcome") {
                val formattedGoal = termFormatter.format(initialGoal.value)
                fromString("You should generate a list of plans to pursue the goal `$formattedGoal`.")
                fromFile("expectedOutcome.md")
            }
        }.buildAsMessage()
    }

    private fun <T> formatAsBulletList(
        items: Collection<T>,
        formatter: (Collection<T>) -> List<String>,
    ): String {
        val res = formatter(items)
        return if (res.isNotEmpty()) {
            res.joinToString(separator = "\n") { "- $it" }
        } else {
            ""
        }
    }
}
