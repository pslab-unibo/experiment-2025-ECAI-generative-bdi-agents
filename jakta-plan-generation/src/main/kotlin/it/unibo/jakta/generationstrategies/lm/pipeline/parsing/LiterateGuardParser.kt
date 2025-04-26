package it.unibo.jakta.generationstrategies.lm.pipeline.parsing

import it.unibo.jakta.agents.bdi.Jakta.toLeftNestedAnd
import it.unibo.jakta.agents.bdi.Prolog2Jakta
import it.unibo.jakta.agents.bdi.beliefs.Belief
import it.unibo.jakta.agents.bdi.beliefs.Belief.Companion.SOURCE_SELF
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.JaktaParser.tangleStruct
import it.unibo.jakta.generationstrategies.lm.pipeline.parsing.impl.PlanData
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth

object LiterateGuardParser {
    /**
     * Parses a guard expression made of multiple conditions into a properly structured `Struct`
     * with `Belief`-wrapped components when appropriate.
     */
    private fun parseCompositeGuard(input: String): Struct? {
        // Try to parse directly first
        val directResult = tangleStruct(input)
        if (directResult != null) {
            return wrapBelief(directResult)
        }

        // Check for composite expressions
        val andPattern = Regex("\\band\\b|&")
        val orPattern = Regex("\\bor\\b|\\|")

        // Handle 'not' expressions first
        if (input.trim().startsWith("not(") || input.trim().startsWith("~(")) {
            val innerContent = extractParenthesisContent(input)
            if (innerContent != null) {
                val innerStruct = parseCompositeGuard(innerContent)
                return innerStruct?.let { Struct.of("~", it) }
            }
        }

        // Handle conjunctions (AND)
        if (andPattern.containsMatchIn(input)) {
            val parts = splitLogicalExpression(input, andPattern)
            if (parts.size >= 2) {
                val parsedParts = parts.mapNotNull { parseCompositeGuard(it.trim()) }
                if (parsedParts.size == parts.size) {
                    // Build a left-nested AND structure
                    return parsedParts.reduce { acc, struct ->
                        Struct.of("&", acc, struct)
                    }
                }
            }
        }

        // Handle disjunctions (OR)
        if (orPattern.containsMatchIn(input)) {
            val parts = splitLogicalExpression(input, orPattern)
            if (parts.size >= 2) {
                val parsedParts = parts.mapNotNull { parseCompositeGuard(it.trim()) }
                if (parsedParts.size == parts.size) {
                    // Build a left-nested OR structure
                    return parsedParts.reduce { acc, struct ->
                        Struct.of("|", acc, struct)
                    }
                }
            }
        }

        return null
    }

    /**
     * Wraps a struct in a Belief and extracts the rule head, or applies negation
     * if the struct represents a negation operation.
     */
    private fun wrapBelief(struct: Struct): Struct {
        return if (struct.functor == "not") {
            val innerStruct = struct.args[0].castToStruct()
            Struct.of("not", wrapAsBeliefHead(innerStruct))
        } else {
            wrapAsBeliefHead(struct)
        }
    }

    /**
     * Wraps a struct in a Belief and extracts the rule head.
     */
    private fun wrapAsBeliefHead(struct: Struct): Struct {
        return Belief.wrap(struct, wrappingTag = SOURCE_SELF).rule.head
    }

    /**
     * Processes a complete guard expression, which might be a composite condition or a simple condition.
     */
    fun processGuard(plan: PlanData): Struct? {
        if (plan.conditions.contains("<none>")) {
            return Truth.TRUE
        }

        // Check if any condition is a composite expression
        val compositeCondition = plan.conditions.find { c ->
            c.contains(" and ") || c.contains(" or ")
        }

        if (compositeCondition != null) {
            val parsedComposite = parseCompositeGuard(compositeCondition)
            return parsedComposite?.accept(Prolog2Jakta)?.castToStruct() ?: Truth.TRUE
        } else {
            val individualConditions = plan.conditions
                .mapNotNull { c -> tangleStruct(c)?.accept(Prolog2Jakta)?.castToStruct() }
                .map { wrapBelief(it) }

            return individualConditions.toLeftNestedAnd()
        }
    }

    private fun parseCompositeStruct(input: String): Struct? {
        // Try to parse directly first
        val directResult = tangleStruct(input)
        if (directResult != null) {
            return directResult
        }

        // Check for composite expressions
        // Regular expressions to identify logical operators
        val andPattern = Regex("\\band\\b|&")
        val orPattern = Regex("\\bor\\b|\\|")

        // Handle 'not' expressions first (they have higher precedence in parsing)
        if (input.trim().startsWith("not(") || input.trim().startsWith("~(")) {
            val innerContent = extractParenthesisContent(input)
            if (innerContent != null) {
                val innerStruct = parseCompositeStruct(innerContent)
                return innerStruct?.let { Struct.of("~", it) }
            }
        }

        // Handle conjunctions (AND)
        if (andPattern.containsMatchIn(input)) {
            val parts = splitLogicalExpression(input, andPattern)
            if (parts.size >= 2) {
                val parsedParts = parts.mapNotNull { parseCompositeStruct(it.trim()) }
                if (parsedParts.size == parts.size) {
                    // Build a left-nested AND structure
                    return parsedParts.reduce { acc, struct ->
                        Struct.of("&", acc, struct)
                    }
                }
            }
        }

        // Handle disjunctions (OR)
        if (orPattern.containsMatchIn(input)) {
            val parts = splitLogicalExpression(input, orPattern)
            if (parts.size >= 2) {
                val parsedParts = parts.mapNotNull { parseCompositeStruct(it.trim()) }
                if (parsedParts.size == parts.size) {
                    // Build a left-nested OR structure
                    return parsedParts.reduce { acc, struct ->
                        Struct.of("|", acc, struct)
                    }
                }
            }
        }

        return null
    }

    private fun extractParenthesisContent(input: String): String? {
        val trimmed = input.trim()
        val notPrefix = if (trimmed.startsWith("not(")) "not(" else if (trimmed.startsWith("~(")) "~(" else return null

        // Find matching closing parenthesis, accounting for nested parentheses
        var open = 1
        var closeIndex = notPrefix.length

        while (open > 0 && closeIndex < trimmed.length) {
            when (trimmed[closeIndex]) {
                '(' -> open++
                ')' -> open--
            }
            closeIndex++
        }

        return if (open == 0 && closeIndex <= trimmed.length) {
            trimmed.substring(notPrefix.length, closeIndex - 1)
        } else {
            null
        }
    }

    private fun splitLogicalExpression(input: String, pattern: Regex): List<String> {
        // Split the expression by the pattern, but respect parentheses nesting
        val parts = mutableListOf<String>()
        var currentPart = StringBuilder()
        var openParens = 0

        var i = 0
        while (i < input.length) {
            when {
                input[i] == '(' -> {
                    openParens++
                    currentPart.append(input[i])
                }
                input[i] == ')' -> {
                    openParens--
                    currentPart.append(input[i])
                }
                openParens == 0 && i + 3 <= input.length &&
                    pattern.matches(input.substring(i, i + 3)) -> {
                    // Found "and" or "or" operator outside parentheses
                    parts.add(currentPart.toString())
                    currentPart = StringBuilder()
                    i += 2 // Skip the operator
                }
                openParens == 0 && i + 1 < input.length &&
                    (input.substring(i, i + 1) == "&" || input.substring(i, i + 1) == "|") -> {
                    // Found & or | operator outside parentheses
                    parts.add(currentPart.toString())
                    currentPart = StringBuilder()
                }
                else -> {
                    currentPart.append(input[i])
                }
            }
            i++
        }

        if (currentPart.isNotEmpty()) {
            parts.add(currentPart.toString())
        }

        return parts
    }
}
