package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl

import com.charleskorn.kaml.Yaml
import it.unibo.jakta.agents.bdi.Jakta.toLeftNestedAnd
import it.unibo.jakta.agents.bdi.Prolog2Jakta
import it.unibo.jakta.agents.bdi.beliefs.AdmissibleBelief
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.Belief.Companion.SOURCE_SELF
import it.unibo.jakta.agents.bdi.events.AchievementGoalInvocation
import it.unibo.jakta.agents.bdi.events.AdmissibleGoal
import it.unibo.jakta.agents.bdi.events.Trigger
import it.unibo.jakta.agents.bdi.goals.EmptyGoal
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.JaktaParser.tangleGoal
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.JaktaParser.tangleStruct
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.JaktaParser.tangleTrigger
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.Parser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.TemplateData.BeliefTemplate
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.TemplateData.GoalTemplate
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserFailure.EmptyResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserFailure.GenericParserFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserSuccess.NewPlan
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserSuccess.NewResult
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import kotlinx.serialization.builtins.ListSerializer

class ParserImpl : Parser {
    private val yaml = Yaml.default

    override fun parse(input: String): ParserResult {
        val blocks = extractCodeBlocks(input)
        val newPlans = mutableListOf<NewPlan>()
        val newAdmissibleBeliefs = mutableSetOf<AdmissibleBelief>()
        val newAdmissibleGoals = mutableSetOf<AdmissibleGoal>()

        val parsedBlocks = blocks.map { content ->
            try {
                parsePlanData(content) ?: run {
                    try {
                        yaml.decodeFromString(PlanData.serializer(), content)
                    } catch (_: Exception) {
                        try {
                            yaml.decodeFromString(ListSerializer(TemplateData.serializer()), content)
                        } catch (_: Exception) {
                            return GenericParserFailure("Unsupported block format inside ticks")
                        }
                    }
                }
            } catch (e: Exception) {
                return GenericParserFailure("Error parsing content: ${e.message}")
            }
        }

        val error = parsedBlocks.filterIsInstance<GenericParserFailure>().firstOrNull()
        return if (error != null) {
            error
        } else {
            parsedBlocks.forEach {
                when (it) {
                    is List<*> -> it.filterIsInstance<TemplateData>().forEach { template ->
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
                                val trigger = parseTriggerWithAchieveFallback(template.goal)
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

    private fun parsePlanData(content: String): PlanData? {
        val lines = content.lines()

        var event = ""
        val conditions = mutableListOf<String>()
        val operations = mutableListOf<String>()

        var currentSection = ""

        for (line in lines) {
            val trimmedLine = line.trim()

            if (trimmedLine.isEmpty()) continue

            when {
                trimmedLine.startsWith("EVENT:") -> {
                    event = trimmedLine.substringAfter("EVENT:").trim()
                    currentSection = "EVENT"
                }
                trimmedLine.startsWith("CONDITIONS:") -> {
                    currentSection = "CONDITIONS"
                }
                trimmedLine.startsWith("OPERATIONS:") -> {
                    currentSection = "OPERATIONS"
                }
                // Handle list items or values within the current section
                else -> {
                    when (currentSection) {
                        "CONDITIONS" -> {
                            if (trimmedLine == "<none>") {
                                conditions.add("<none>")
                            } else if (trimmedLine.startsWith("-")) {
                                conditions.add(trimmedLine.substringAfter("-").trim())
                            }
                        }
                        "OPERATIONS" -> {
                            if (trimmedLine == "<none>") {
                                operations.add("<none>")
                            } else if (trimmedLine.startsWith("-")) {
                                operations.add(trimmedLine.substringAfter("-").trim())
                            }
                        }
                    }
                }
            }
        }

        return if (event.isNotEmpty()) {
            PlanData(event, conditions, operations)
        } else {
            null
        }
    }

    private fun convertToPlan(plan: PlanData): NewPlan? {
        val trigger = parseTriggerWithAchieveFallback(plan.event)

        val guard = if (plan.conditions.contains("<none>")) {
            Truth.TRUE
        } else {
            plan.conditions
                .mapNotNull { c -> tangleStruct(c)?.accept(Prolog2Jakta)?.castToStruct() }
                .map {
                    if (it.functor == "~") {
                        val struct = it.args[0].castToStruct()
                        Struct.of("~", Belief.wrap(struct, wrappingTag = SOURCE_SELF).rule.head)
                    } else {
                        Belief.wrap(it, wrappingTag = SOURCE_SELF).rule.head
                    }
                }
                .toLeftNestedAnd()
        }

        val goals = if (plan.operations.contains("<none>")) {
            listOf(EmptyGoal())
        } else {
            plan.operations.mapNotNull { op -> tangleGoal(op) }
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
        val regex = """```(?:\w*\n)?([^`]*?)```""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val matches = regex.findAll(input)

        return matches.flatMap { matchResult ->
            val blockContent = matchResult.groupValues[1].trim()
            if (blockContent.contains("\n---\n")) {
                blockContent.split("\n---\n").map { it.trim() }
            } else {
                listOf(blockContent)
            }
        }.toList()
    }
}
