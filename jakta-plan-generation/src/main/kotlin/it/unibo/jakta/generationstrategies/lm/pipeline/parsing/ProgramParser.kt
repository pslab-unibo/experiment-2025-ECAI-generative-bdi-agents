package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.ProgramParserImpl
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.result.ParserResult.PlanGenerationParserResult
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.PlanProcessor
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.SimpleProcessor
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.TagProcessor
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.tagprocessor.TagType

interface ProgramParser : ResponseParser {
    val tagParser: TagStreamingParser
    val processors: Map<TagType, TagProcessor>
    val stopTags: List<TagType>

    override fun buildResult(): PlanGenerationParserResult

    fun copy(
        tagParser: TagStreamingParser = this.tagParser,
        processors: Map<TagType, TagProcessor> = this.processors,
        stopTags: List<TagType> = this.stopTags,
    ): ProgramParser

    fun setOnTagCompleteListener(processors: Map<TagType, TagProcessor>): ProgramParser

    companion object {
        fun of(): ProgramParser {
            val tagParser = TagStreamingParser.of()
            val processors = mapOf(
                TagType.Step to SimpleProcessor(),
                TagType.Plan to PlanProcessor(),
            )
            val stopTags = listOf(TagType.Step)
            return ProgramParserImpl(tagParser, processors, stopTags)
                .setOnTagCompleteListener(processors)
        }
    }
}
