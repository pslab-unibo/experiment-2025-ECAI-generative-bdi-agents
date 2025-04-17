package it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl

import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.PlanStreamingParser
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.PlanStreamingParser.ParsedPlan

class PlanStreamingParserImpl : PlanStreamingParser {
    private enum class State {
        INITIAL,
        PARSING_TRIGGER,
        AFTER_TRIGGER,
        PARSING_ONLY_IF,
        PARSING_CONDITIONS,
        AFTER_CONDITIONS,
        PARSING_THEN,
        PARSING_GOALS,
    }

    private var state = State.INITIAL
    private val currentBuffer = StringBuilder()
    private var backtickLevel = 0

    private var trigger: String? = null
    private val conditions = mutableListOf<String>()
    private val goals = mutableListOf<String>()
    private var isParsingFragment = false

    override fun parse(c: Char) {
        currentBuffer.append(c)

        when (state) {
            State.INITIAL -> handleInitialState(c)
            State.PARSING_TRIGGER -> handleParsingTrigger(c)
            State.AFTER_TRIGGER -> handleAfterTrigger()
            State.PARSING_ONLY_IF -> handleParsingOnlyIf()
            State.PARSING_CONDITIONS -> handleParsingConditions(c)
            State.AFTER_CONDITIONS -> handleAfterConditions()
            State.PARSING_THEN -> handleParsingThen()
            State.PARSING_GOALS -> handleParsingGoals(c)
        }
    }

    private fun handleBacktick() {
        if (isParsingFragment) {
            backtickLevel--
            if (backtickLevel == 0) {
                isParsingFragment = false
                currentBuffer.deleteCharAt(currentBuffer.length - 1)
                handleFragmentComplete()
            }
        } else {
            backtickLevel++
            isParsingFragment = true
            currentBuffer.clear()
        }
    }

    private fun handleInitialState(c: Char) {
        if (c == '`') {
            handleBacktick()
            state = State.PARSING_TRIGGER
        }
    }

    private fun handleParsingTrigger(c: Char) {
        if (c == '`') {
            handleBacktick()
            if (!isParsingFragment) {
                state = State.AFTER_TRIGGER
            }
        }
    }

    private fun handleAfterTrigger() {
        val text = currentBuffer.toString().trim()
        if (text.startsWith("only if")) {
            currentBuffer.clear()
            state = State.PARSING_ONLY_IF
        }
    }

    private fun handleParsingOnlyIf() {
        val text = currentBuffer.toString().trim()
        if (text.isNotEmpty() && text.last() == '`') {
            handleBacktick()
            state = State.PARSING_CONDITIONS
        }
    }

    private fun handleParsingConditions(c: Char) {
        if (c == '`') {
            handleBacktick()
        } else if (!isParsingFragment) {
            val text = currentBuffer.toString().trim()
            if (text.endsWith("then")) {
                currentBuffer.clear()
                state = State.PARSING_THEN
            }
        }
    }

    private fun handleAfterConditions() {
        val text = currentBuffer.toString().trim()
        if (text.startsWith("then")) {
            currentBuffer.clear()
            state = State.PARSING_THEN
        }
    }

    private fun handleParsingThen() {
        val text = currentBuffer.toString().trim()
        if (text.isNotEmpty() && text.last() == '`') {
            handleBacktick()
            state = State.PARSING_GOALS
        }
    }

    private fun handleParsingGoals(c: Char) {
        if (c == '`') {
            handleBacktick()
        }
    }

    private fun handleFragmentComplete() {
        val content = currentBuffer.toString()

        when (state) {
            State.PARSING_TRIGGER -> trigger = content
            State.PARSING_CONDITIONS -> conditions.add(content)
            State.PARSING_GOALS -> goals.add(content)
            else -> {}
        }

        currentBuffer.clear()
    }

    override fun getParsedPlan(): ParsedPlan? {
        return trigger?.let {
            ParsedPlan(
                it,
                conditions.toList(),
                goals.toList(),
            )
        }
    }

    override fun parse(input: String) = input.forEach { parse(it) }

    override fun reset() {
        state = State.INITIAL
        currentBuffer.clear()
        backtickLevel = 0
        trigger = null
        conditions.clear()
        goals.clear()
        isParsingFragment = false
    }
}
