package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.TagStreamingParser

class TagStreamingParserImpl : TagStreamingParser {

    enum class State { TEXT, OPEN_TAG, CONTENT, CLOSE_TAG }

    private var state = State.TEXT
    private var listener: ((String, String) -> Unit)? = null

    override val tagNameBuffer = StringBuilder()
    override val contentBuffer = StringBuilder()
    private val closingTagBuffer = StringBuilder()

    private var currentTagName = ""
    private var pendingLessThan = false

    override fun parse(char: Char) {
        if (pendingLessThan) {
            pendingLessThan = false
            if (char == '/') {
                state = State.CLOSE_TAG
                closingTagBuffer.clear()
                return // already handled the '<' and '/'
            } else {
                // False alarm, '<' was part of content
                if (state == State.CONTENT) contentBuffer.append('<')
                parse(char) // reprocess this char normally
                return
            }
        }

        when (state) {
            State.TEXT -> {
                if (char == '<') {
                    state = State.OPEN_TAG
                    tagNameBuffer.clear()
                }
            }

            State.OPEN_TAG -> {
                if (char == '>') {
                    currentTagName = tagNameBuffer.toString()
                    contentBuffer.clear()
                    state = State.CONTENT
                } else {
                    tagNameBuffer.append(char)
                }
            }

            State.CONTENT -> {
                if (char == '<') {
                    pendingLessThan = true // Wait to see if it's a closing tag
                } else {
                    contentBuffer.append(char)
                }
            }

            State.CLOSE_TAG -> {
                if (char == '>') {
                    val closingTag = closingTagBuffer.toString()
                    if (closingTag == currentTagName) {
                        listener?.invoke(currentTagName, contentBuffer.toString())
                    }
                    reset()
                } else {
                    closingTagBuffer.append(char)
                }
            }
        }
    }

    override fun parse(input: String) = input.forEach { parse(it) }

    override fun reset() {
        tagNameBuffer.clear()
        contentBuffer.clear()
        closingTagBuffer.clear()
        currentTagName = ""
        pendingLessThan = false
        state = State.TEXT
    }

    override fun isTagOpen(): Boolean {
        return state == State.CONTENT
    }

    override fun setOnTagCompleteListener(listener: (tagName: String, content: String) -> Unit) {
        this.listener = listener
    }
}
