package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.TagStreamingParserImpl

interface TagStreamingParser : Parser {
    val tagNameBuffer: StringBuilder
    val contentBuffer: StringBuilder

    fun isTagOpen(): Boolean

    fun setOnTagCompleteListener(listener: (tagName: String, content: String) -> Unit)

    companion object {
        fun of() = TagStreamingParserImpl()
    }
}
