package it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.impl

import com.charleskorn.kaml.Yaml
import it.unibo.jakta.agents.bdi.engine.Jakta.dropBackticks
import it.unibo.jakta.agents.bdi.engine.Jakta.dropSquareBrackets
import it.unibo.jakta.agents.bdi.engine.Jakta.removeColonsFromQuotedStrings
import it.unibo.jakta.agents.bdi.engine.JaktaParser.tangleGoal
import it.unibo.jakta.agents.bdi.engine.JaktaParser.tangleStruct
import it.unibo.jakta.agents.bdi.engine.JaktaParser.tangleTrigger
import it.unibo.jakta.agents.bdi.engine.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.engine.beliefs.Belief.Companion.SOURCE_SELF
import it.unibo.jakta.agents.bdi.engine.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.engine.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.engine.events.Trigger
import it.unibo.jakta.agents.bdi.engine.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.engine.plans.PlanID
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.GuardParser.processGuard
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.EmptyResponse
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserFailure.GenericParserFailure
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserSuccess.NewPlan
import it.unibo.jakta.agents.bdi.generationstrategies.lm.pipeline.parsing.result.ParserSuccess.NewResult
import kotlinx.serialization.builtins.ListSerializer

internal class ParserImpl : Parser {
    private val yaml = Yaml.default

    override fun parse(input: String): ParserResult {
        val blocks = extractCodeBlocks(input)
        val newPlans = mutableListOf<NewPlan>()
        val newAdmissibleBeliefs = mutableSetOf<AdmissibleBelief>()
        val newAdmissibleGoals = mutableSetOf<AdmissibleGoal>()

        val parsedBlocks =
            blocks.map { content ->
                val processedContent =
                    content
                        .dropBackticks()
                        .dropSquareBrackets()
                        .removeColonsFromQuotedStrings()

                try {
                    yaml.decodeFromString(PlanData.serializer(), processedContent)
                } catch (_: Exception) {
                    if (processedContent.trim() == "- <none>" || processedContent.trim() == "<none>") {
                        emptyList()
                    } else {
                        try {
                            yaml.decodeFromString(ListSerializer(TemplateData.serializer()), processedContent)
                        } catch (_: Exception) {
                            emptyList()
                        }
                    }
                }
            }

        val error = parsedBlocks.filterIsInstance<GenericParserFailure>().firstOrNull()
        return if (error != null) {
            error
        } else {
            parsedBlocks.forEach {
                when (it) {
                    is List<*> ->
                        it.filterIsInstance<TemplateData>().forEach { template ->
                            when (template) {
                                is BeliefTemplate -> {
                                    val struct = tangleStruct(template.belief)
                                    struct?.let { v ->
                                        newAdmissibleBeliefs.add(
                                            AdmissibleBelief.wrap(
                                                v,
                                                wrappingTag = SOURCE_SELF,
                                                purpose = template.purpose,
                                            ),
                                        )
                                    }
                                }
                                is GoalTemplate -> {
                                    val trigger =
                                        parseTriggerWithAchieveFallback(template.goal)
                                            ?.copy(purpose = template.purpose)
                                    trigger?.let { t -> newAdmissibleGoals.add(AdmissibleGoal(t)) }
                                }
                            }
                        }
                    is PlanData -> convertToPlan(it)?.let { p -> newPlans.add(p) }
                    else -> it
                }
            }

            if (newPlans.isEmpty() && newAdmissibleBeliefs.isEmpty() && newAdmissibleGoals.isEmpty()) {
                EmptyResponse(input)
            } else {
                NewResult(newPlans, newAdmissibleGoals, newAdmissibleBeliefs, input)
            }
        }
    }

    private fun convertToPlan(plan: PlanData): NewPlan? {
        val trigger = parseTriggerWithAchieveFallback(plan.event)
        val guard = processGuard(plan)
        val goals =
            plan.operations.mapNotNull {
                if (it.contains("<none>")) {
                    EmptyGoal()
                } else {
                    tangleGoal(it)
                }
            }

        return if (trigger != null && guard != null) {
            NewPlan(
                id = PlanID(trigger, guard),
                trigger = trigger,
                guard = guard,
                goals = goals,
            )
        } else {
            null
        }
    }

    /**
     * Tries to parse the input as a [Trigger] or as a [AchievementGoalInvocation].
     */
    private fun parseTriggerWithAchieveFallback(input: String): Trigger? =
        tangleTrigger(input) ?: tangleStruct(input)?.let { AchievementGoalInvocation(it) }

    private fun extractCodeBlocks(input: String): List<String> {
        val regex = """```(?:\w*\n)?([\s\S]*?)```""".toRegex()
        val matches = regex.findAll(input)

        return matches
            .flatMap { matchResult ->
                val blockContent = matchResult.groupValues[1].trim()
                if (blockContent.contains("\n---\n")) {
                    blockContent.split("\n---\n").map { it.trim() }
                } else if (blockContent.contains("\nEVENT: ")) {
                    // Handle YAML format with multiple events in one block
                    val eventBlocks =
                        blockContent
                            .split("\nEVENT: ")
                            .filter { it.isNotEmpty() }
                            .map { if (it.startsWith("EVENT: ")) it else "EVENT: $it" }
                    eventBlocks
                } else {
                    listOf(blockContent)
                }
            }.toList()
    }
}
