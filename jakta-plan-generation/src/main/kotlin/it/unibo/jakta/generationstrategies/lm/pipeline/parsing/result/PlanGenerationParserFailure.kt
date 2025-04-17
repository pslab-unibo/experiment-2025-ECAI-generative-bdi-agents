package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.ParserFailure
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.PlanGenerationParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.TagType

sealed interface PlanGenerationParserFailure : ParserFailure, PlanGenerationParserResult {
    data class ProcessorFailure(val tagType: TagType, override val rawContent: String) : PlanGenerationParserFailure

    data class UnknownTagType(val tagType: TagType, override val rawContent: String) : PlanGenerationParserFailure

    data class IncompleteTagParsing(
        val incompleteTag: TagType,
        override val rawContent: String,
    ) : PlanGenerationParserFailure

    data class InvalidGuard(override val rawContent: String) : PlanGenerationParserFailure

    data class InvalidTrigger(override val rawContent: String) : PlanGenerationParserFailure

    data class InvalidGoal(override val rawContent: String) : PlanGenerationParserFailure

    data class InvalidStep(override val rawContent: String) : PlanGenerationParserFailure
}
