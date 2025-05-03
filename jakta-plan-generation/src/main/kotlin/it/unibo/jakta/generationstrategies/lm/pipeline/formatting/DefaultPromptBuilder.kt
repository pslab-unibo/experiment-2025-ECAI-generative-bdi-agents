package it.unibo.jakta.generationstrategies.lm.pipeline.formatting

import it.unibo.jakta.agents.bdi.Jakta.dropNumbers
import it.unibo.jakta.agents.bdi.Jakta.removeSource
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.actionsFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.admissibleBeliefsFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.admissibleGoalsFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.beliefsFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.goalFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.termFormatter
import it.unibo.jakta.agents.bdi.formatters.DefaultFormatters.triggerFormatter
import it.unibo.jakta.agents.bdi.goals.Test
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder.Companion.formatAsBulletList
import it.unibo.jakta.generationstrategies.lm.pipeline.formatting.PromptBuilder.Companion.prompt

object DefaultPromptBuilder {
    val descriptivePrompt = prompt { ctx ->
        section("Background") { fromFile("background.md") }

        section("Agent's internal state") {
            section("Beliefs") {
                section("Admissible beliefs") {
                    fromFormatter(ctx.admissibleBeliefs) {
                        formatAsBulletList(it, admissibleBeliefsFormatter::format)
                    }
                }

                section("Actual beliefs") {
                    fromFormatter(ctx.actualBeliefs.asIterable().toList()) {
                        formatAsBulletList(it, beliefsFormatter::format)
                    }
                }
            }

            section("Goals") {
                section("Admissible goals") {
                    fromFormatter(ctx.admissibleGoals) {
                        formatAsBulletList(it, admissibleGoalsFormatter::format)
                    }
                }

                section("Actual goals") {
                    fromFormatter(ctx.actualGoals) { plans ->
                        val triggers = plans.map { it.trigger }
                        formatAsBulletList(triggers, triggerFormatter::format)
                    }
                }
            }

            section("Admissible actions") {
                fromFormatter(ctx.internalActions + ctx.externalActions) {
                    formatAsBulletList(it, actionsFormatter::format)
                }
            }

            section("Remarks") {
                fromFormatter(ctx.remarks) { r ->
                    formatAsBulletList(r) { it.map { r -> r.value } }
                }
            }
        }

        section("Expected outcome") {
            val formattedGoal = if (ctx.initialGoal.goal is Test) {
                "test ${termFormatter.format(ctx.initialGoal.goal.value.removeSource()).dropNumbers()}"
            } else {
                goalFormatter.format(ctx.initialGoal.goal)
            }
            fromString(
                "You must output only the final set of plans to pursue the goal $formattedGoal, " +
                    "with no alternatives.",
            )
            fromFile("expectedOutcome.md")
        }
    }
}
