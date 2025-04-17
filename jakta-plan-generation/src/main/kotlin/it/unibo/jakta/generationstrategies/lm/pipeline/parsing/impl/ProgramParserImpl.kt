package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl

import it.unibo.jakta.agents.bdi.Jakta.capitalize
import it.unibo.jakta.agents.bdi.Jakta.toLeftNestedAnd
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.processArithmeticExpressions
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleGoal
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleStruct
import it.unibo.jakta.agents.bdi.parsing.LiteratePrologParser.tangleTrigger
import it.unibo.jakta.agents.bdi.plans.LiteratePlan
import it.unibo.jakta.agents.bdi.plans.PlanID
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsedStatement
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsedStatement.PlanStatement
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ParsedStatement.SimpleStatement
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.ProgramParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.TagStreamingParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.PlanGenerationParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserFailure.UnknownTagType
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.CompositeParserSuccess
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.EmptyResponse
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.NewPlan
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.PlanGenerationParserSuccess.NewStep
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.TagProcessor
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.TagType
import kotlin.collections.get

class ProgramParserImpl(
    override val tagParser: TagStreamingParser,
    override val processors: Map<TagType, TagProcessor>,
    override val stopTags: List<TagType>,
) : ProgramParser {

    private var completed = false
    private val contentBuffer = StringBuilder()
    private val errors = mutableListOf<PlanGenerationParserFailure>()

    override fun copy(
        tagParser: TagStreamingParser,
        processors: Map<TagType, TagProcessor>,
        stopTags: List<TagType>,
    ) = ProgramParserImpl(tagParser, processors, stopTags)

    override fun setOnTagCompleteListener(processors: Map<TagType, TagProcessor>): ProgramParser {
        tagParser.setOnTagCompleteListener { tagName, content ->
            if (!completed) {
                val parsedTagName = TagType.fromString(tagName.capitalize())
                val processor = processors[parsedTagName]

                if (processor != null) {
                    val res = processor.process(content)
                    if (!res) {
                        parsedTagName?.let { errors.add(PlanGenerationParserFailure.ProcessorFailure(it, content)) }
                    }
                } else {
                    parsedTagName?.let { errors.add(UnknownTagType(it, content)) }
                }

                if (TagType.fromString(tagName) in stopTags) {
                    completed = true
                }
            }
        }
        return this.copy(processors = processors)
    }

    /**
     * The parser terminates when a stop tag is met.
     */
    override fun isComplete(): Boolean = completed

    override fun parse(input: String) = input.forEach { parse(it) }

    override fun reset() {
        tagParser.reset()
        contentBuffer.clear()
        errors.clear()
        completed = false
    }

    override fun parse(input: Char) {
        contentBuffer.append(input)
        tagParser.parse(input)
    }

    /**
     * Maps [ParsedStatement]s to [PlanGenerationParserSuccess]s
     *
     * Also reports any errors encountered during parsing
     */
    override fun buildResult(): PlanGenerationParserResult {
        // TODO stop at first error or collect all of them first?
        if (tagParser.isTagOpen()) {
            val tagName = tagParser.tagNameBuffer.toString()
            val parsedTagName = TagType.fromString(tagName.capitalize())
            parsedTagName?.let {
                errors.add(
                    PlanGenerationParserFailure.IncompleteTagParsing(
                        it,
                        tagParser.contentBuffer.toString(),
                    ),
                )
            }
        }

        val plans = getItems(TagType.Plan) { it as? PlanStatement }
        val step = getItems(TagType.Step) { it as? SimpleStatement }

        val newPlans = plans.mapNotNull {
            val extraGuards = mutableListOf<String>()
            val processedGoals = it.goals.map { g ->
                val processedGoal = processArithmeticExpressions(g)
                extraGuards.addAll(processedGoal.second)
                processedGoal.first
            }

            val guards = it.guards + extraGuards
            val guard = guards.mapNotNull { guard ->
                val res = tangleStruct(guard)
                if (res == null) {
                    errors.add(PlanGenerationParserFailure.InvalidGuard(guard))
                }
                res
            }.toLeftNestedAnd()

            val trigger = tangleTrigger(it.trigger)

            if (guard != null && trigger != null) {
                val goals = processedGoals.mapNotNull { goal ->
                    val res = tangleGoal(goal)
                    if (res == null) {
                        errors.add(PlanGenerationParserFailure.InvalidGoal(goal))
                    }
                    res
                }

                val parts = it.rawContent.split("only if", "then").map { it.trim() }
                val literateTrigger = parts[0]
                val literateGuards = parts[1]
                val literateGoals = parts[2]

                NewPlan(
                    LiteratePlan.of(
                        PlanID.of(trigger, guard),
                        trigger,
                        guard,
                        goals,
                        literateTrigger,
                        literateGuards,
                        literateGoals,
                    ),
                    it.rawContent.trim(),
                )
            } else {
                if (guard == null) {
                    errors.add(PlanGenerationParserFailure.InvalidTrigger(it.trigger))
                }
                if (trigger == null) {
                    errors.add(PlanGenerationParserFailure.InvalidGuard(guards.joinToString()))
                }
                null
            }
        }

        val newStep = step.mapNotNull {
            val res = tangleGoal(it.parsedContent)
            if (res == null) {
                errors.add(PlanGenerationParserFailure.InvalidStep(it.parsedContent))
                null
            } else {
                NewStep(res, it.rawContent)
            }
        }

        val res = when {
            errors.isNotEmpty() -> errors.first() // TODO report only one error at a time?
            newPlans.isNotEmpty() && newStep.isNotEmpty() -> CompositeParserSuccess(newStep.first(), newPlans)
            newPlans.isNotEmpty() -> CompositeParserSuccess(newPlans = newPlans)
            newStep.isNotEmpty() -> newStep.first()
            else -> EmptyResponse(contentBuffer.toString())
        }

        reset()

        return res
    }

    private fun <T> getItems(tagType: TagType, transform: (Any) -> T?): List<T> {
        return processors[tagType]?.getItems()?.mapNotNull(transform) ?: emptyList()
    }
}
